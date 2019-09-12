# De-register for VAT Frontend

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/hmrc/deregister-vat-frontend.svg)](https://travis-ci.org/hmrc/deregister-vat-frontend)
[![Download](https://api.bintray.com/packages/hmrc/releases/deregister-vat-frontend/images/download.svg)](https://bintray.com/hmrc/releases/deregister-vat-frontend/_latestVersion)

## Summary

This service provides end users with a mechanism to De-register for VAT.

## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.

## Running
`sbt "run 9153 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes"`

## Testing
`sbt clean coverage test it:test coverageReport`

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
