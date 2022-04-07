# vc-sl-sdk-java

Support for the Verifiable Credential Status List on the Hedera File Service using JAVA SDK.

This repository contains the SDK for managing [Verifiable Credential Status List 2021](https://w3c-ccg.github.io/vc-status-list-2021) using the [Hedera File Service](https://docs.hedera.com/guides/docs/sdks/file-storage).

vc-sl-sdk-java based on [vc-sl-sdk-js], so both of them contain similar methods and classes.

## Usage

### Dependency Declaration

#### Maven

```xml

<dependency>
    <groupId>com.hedera.hashgraph</groupId>
    <artifactId>vc-sl-sdk-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Gradle

```gradle
implementation 'com.hedera.hashgraph:vc-sl-sdk-java:1.0.0'
```

## Setup Hedera Portal Account

- Register hedera portal Testnet account <https://portal.hedera.com/register>
- Login to portal <https://portal.hedera.com/?network=testnet>
- Obtain accountId & privateKey string value.

```json
{
  "operator": {
    "accountId": "0.0.xxxx",
    "publicKey": "...",
    "privateKey": "302.."
  }
}
```

- Following examples use accountId as `OPERATOR_ID` and privateKey string value as `OPERATOR_KEY` to submit DID Event
  Messages to HCS.

## Examples

Sample demo step by step JAVA Test example are available at [Demo Test Folder][demo-location]. Make sure to add
appropriate `testnet` account details in <b>`lib/src/test/resources/demo.config.properties`</b>

- OPERATOR_ID=0.0.xxxx
- OPERATOR_KEY=302...

and make sure DID information is set to empty at the moment:

- VC_STATUS_LIST_OWNER_PRIVATE_KEY=
- VC_STATUS_LIST_FILE_ID=

## Setup 1 Create status list

```shell
gradle clean demoTests --tests demo.DemoTest.create -i
```

After running `create` test of the demo test flow use printed out values to complete
the <b>`lib/src/test/resources/demo.config.properties`</b> configuration file.

- VC_STATUS_LIST_FILE_ID=0.0.xxx
- VC_STATUS_LIST_OWNER_PRIVATE_KEY=302...

That's it! You are set to execute other demo test flows.

## Revoke

```sh
# Revoked by index position 0
gradle clean demoTests --tests demo.DemoTest.revokeByIndex -i

# Resolve status list
gradle clean demoTests --tests demo.DemoTest.resolve -i

```

## Suspend

```sh

# Suspend by index position 0
gradle clean demoTests --tests demo.DemoTest.suspendByIndex -i

# Resolve status list
gradle clean demoTests --tests demo.DemoTest.resolve -i


```

## Resume

```sh

# Resume by index position 0
gradle clean demoTests --tests demo.DemoTest.resumeByIndex -i

# Resolve status list
gradle clean demoTests --tests demo.DemoTest.resolve -i

```

## Active

```sh

# Active by index position 0
gradle clean demoTests --tests demo.DemoTest.activeByIndex -i

# Resolve status list
gradle clean demoTests --tests demo.DemoTest.resolve -i

```


## Development

```sh
git clone git@github.com:hashgraph/vc-sl-sdk-java.git
```

Run build in dev mode (with sourcemap generation and following changes)

```sh
gradle clean build
```

## Tests

Run Unit Tests

```sh
gradle clean test

```

Run Integration Test

Open `lib/src/test/resources/demo.config.properties` file and update the following environment variables with
your `testnet` account details

```js
OPERATOR_ID = "0.0.xxxxxx";
OPERATOR_KEY = "302e02...";
```

```sh
 gradle clean integrationTests
```

## References

- <https://github.com/hashgraph/hedera-sdk-java>
- <https://docs.hedera.com/hedera-api/>
- <https://www.hedera.com/>
- <https://w3c-ccg.github.io/vc-status-list-2021/#statuslist2021entry>

## License Information

Licensed under _license placeholder_.


[demo-location]: https://github.com/Meeco/vc-sl-sdk-java/tree/develop/lib/src/test/java/demo/DemoTest.java

[vc-sl-sdk-js]: https://github.com/Meeco/vc-sl-sdk-js