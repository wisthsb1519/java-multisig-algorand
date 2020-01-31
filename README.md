
# Java AlgoSDK Multisig Project

## Quickstart

1. Find the algod network address from algod.net file within the data
   directory of your algod install. Replace 127.0.0.1:8080 in the mvn
   command with the address from the file.

2. Find the X-Algo-API-Token from algod.token file within the data
   directory of your algod install. Replace ***X-Algo-API-Token*** in the
   mvn command with the token from the file.

To run the example using maven:
```
cd examples
mvn clean install -Dexec.cleanupDaemonThreads=false exec:java -Dexec.mainClass="com.algorand.algosdk.example.MultisigAccount" -Dexec.args="127.0.0.1:8080 ***X-Algo-API-Token***"
```
Output (for instance):
```
Account 1 Address: T4V4ABK7G76Z5X2XMRTRK46D5ZRZ5R7KM5FWCRDARIUJS3NOTL5IRAORI4
Account 2 Address: YVXOQCAQSZR7MMDYFDFUGGATIBWH2LP6KGQA4Y2HR2BRFBXTXFGBY7VDME
Account 3 Address: 7KGD4K4SXOYSMLNR7HNK4UREASZW6TRRA2BW6WMRUALHII3ELJXG4XC6CY
Multisig Address: IBMPXWW2ROLLPOJGFTUWD53ZXWZ4SKSQWHGZUYMSKKLMCR6XL4WZJDUTUM
Please go to: https://bank.testnet.algorand.network/ to fund your multisig account. 
IBMPXWW2ROLLPOJGFTUWD53ZXWZ4SKSQWHGZUYMSKKLMCR6XL4WZJDUTUM

Successfully sent tx group with first tx id: class TransactionID {
    txId: IVMZP4Z35Z2WD46UGNFFHAF26NQXIJV6P3DIN2LBRQDMMGCWCJXA
}
```

## Notes
This example depends transitively on Bouncy Castle through the SDK's direct dependency.
For best practice, it is worth making this an explicit dependency.
