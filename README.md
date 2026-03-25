# De-register for VAT Frontend

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Summary

This service provides end users with a mechanism to De-register for VAT.

## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.

## Running Locally

### Prerequisites
Start required services locally as follows:

```bash
sm2 --start VAT_SUBSCRIPTION_DYNAMIC_STUB DIGITAL_COMMS_DYNAMIC_STUB VAT_AGENT_CLIENT_LOOKUP_FRONTEND FINANCIAL_TRANSACTIONS CHANGE_VAT_ALL
```

### Populating stub data
To run you will need to first populate the dynamic stubs with data via [VAT View & Change Stub Data](https://github.com/hmrc/vat-view-change-stub-data)
by running the `./populate_stub.sh` script. See README.md of the repo for full information.

### Starting the service

```bash
sm2 --stop DEREGISTER_VAT_FRONTEND
./run.sh
```

### Local URL
Once the service is running the first page of the journey is on:
`http://localhost:9153/vat-through-software/account/cancel-vat`

## Testing
`sbt clean coverage test it:test coverageReport`

### Acceptance and Performance Tests
[change-vat-acceptance-tests](https://github.com/hmrc/change-vat-acceptance-tests)

[change-vat-performance-tests](https://github.com/hmrc/change-vat-performance-tests)


## License

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
