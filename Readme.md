# README
### Facts
- Pact broker is deployed on an AWS EC2 machine inside a docker container and
  security groups, etc. are configured to expose it to the world
- In consumer service, the branch name in CI pipeline is received by using `tj-actions/branch-names@v7`
  Github Action and storing it as environment variable. By simply using CLI command from Gradle,
  it wasn't working as expected, as during the build GHA creates a temporary environment and does
  some temporary branch merging, etc. making the branch and commit hash codes differ from the
  actual expected branch and commit hash codes.
### What works in CI
- Consumer Pact publishing to the pact-broker
- Separate `pactTest` task and test directory in gradle
- A job configured in workflow.yml (`run-pact-provider-verification-tests`) that runs pact tests
- Webhook created in pact broker to trigger the provider verification, i.e. triggering CI workflow
  (both `build` + `run-pact-provider-verification-tests` jobs) of the provider
- Provider verification results publishing
- Curently it is possible to publish verification results from the local machine
  (ideally should be prohibited in real world scenario)

## How to test

1. Go to `GetProductRequestContractTest` in consumer-service
2. Create a new branch
3. After line 33 add some new expected field `.stringValue("newField2", "newField2")`
4. Commit and push
5. Go to pact broker UI and after waiting for a couple of minutes, you will see the `Webhook status` change to `1 minute ago`
6. After that you can go to provider service repo's Github actions tab and see that the provider verification job has been triggered and running
7. The job will eventually fail and on the pact broker UI the `Last verified` field will change to `1 minute ago` and is red
8. Now you can add the expected field `val newField2: String = "newField2"` to the default values of `Product` data class
9. Commit and push to the main branch
10. After the workflow with `run-pact-provider-verification-tests` finishes, you can see the `Last verified` field in pact broker is green


## Running provider verification from local machine and publish the results
```
./gradlew pactTest 
```

you can run `can-i-deploy` command from the CLI broker client. Replace the hashcode next to `-e` argument with the commit hash code you want to check
```
./pact-broker can-i-deploy \
      -a provider-service \
      -b http://16.171.86.61 \
      -e befad0c
```

```
./pact-broker can-i-deploy \
      -a consumer-service \
      -b http://16.171.86.61 \
      -e 8cd9e51
```

## From docker cli, the command looks like this, although doesn't work (WIP)
```
docker run --rm \
 -w ${PWD} \
 -v ${PWD}:${PWD} \
 -e PACT_BROKER_BASE_URL=http://16.171.86.61/ \
  --platform linux/x86_64 pactfoundation/pact-cli:latest \
  pact-broker \
  can-i-deploy \
  -a consumer-service \
  -b http://16.171.86.61 \
  --latest
```

# ~~Important~~ (Fixed by removing the pact from broker)
~~Currently, the provider verification job fails on every run, because while playing around with Pact, a breaking contract was published
and somehow got stuck in the broker. And the job outputs~~
```
ProductControllerContractTest > verifyPact(PactVerificationContext, HttpRequest) > Pact between consumer-service (f6050d9) and provider-service - get all products FAILED
    java.lang.AssertionError at ProductControllerContractTest.kt:40

ProductControllerContractTest > verifyPact(PactVerificationContext, HttpRequest) > Pact between consumer-service (a8ef720) and provider-service - get all products PASSED
```
~~So for one version of contract verification always fails, but for the latest version currently it passes and published the latest result.
It's still possible to play around and everything should work as expected, except the provider job failing (but again, on the broker side everything is as expected).
Will need to investigate how this bad contract got stuck and remove it from the broker.~~
