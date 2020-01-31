package com.algorand.algosdk.example;

import com.algorand.algosdk.algod.client.AlgodClient;
import com.algorand.algosdk.algod.client.ApiException;
import com.algorand.algosdk.algod.client.api.AlgodApi;
import com.algorand.algosdk.algod.client.auth.ApiKeyAuth;
import com.algorand.algosdk.account.Account;
import com.algorand.algosdk.crypto.Address;
import com.algorand.algosdk.algod.client.model.TransactionParams;
import com.algorand.algosdk.crypto.Digest;
import com.algorand.algosdk.crypto.Ed25519PublicKey;
import com.algorand.algosdk.algod.client.model.TransactionID;
import com.algorand.algosdk.transaction.Transaction;
import com.algorand.algosdk.util.Encoder;



import com.algorand.algosdk.crypto.MultisigAddress;
import com.algorand.algosdk.transaction.SignedTransaction;


import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class MultisigAccount {

    // Inline class to handle changing block parameters
    // Throughout the example
    static class ChangingBlockParms {
        public BigInteger fee; 
        public BigInteger firstRound;  
        public BigInteger lastRound; 
        public String genID;
        public Digest genHash;
        public ChangingBlockParms() {
            this.fee = BigInteger.valueOf(0);
            this.firstRound = BigInteger.valueOf(0);
            this.lastRound = BigInteger.valueOf(0);
            this.genID = "";
            this.genHash = null;
        }
    };

    // Utility function to update changing block parameters 
    public static ChangingBlockParms getChangingParms(AlgodApi algodApiInstance) throws Exception{
        ChangingBlockParms cp = new MultisigAccount.ChangingBlockParms(); 
        try {
            TransactionParams params = algodApiInstance.transactionParams();
            cp.fee = params.getFee();
            cp.firstRound = params.getLastRound();
            cp.lastRound = cp.firstRound.add(BigInteger.valueOf(1000));
            cp.genID = params.getGenesisID();
            cp.genHash = new Digest(params.getGenesishashb64());

        } catch (ApiException e) {
           throw( e );
        }
        return( cp );
    }

    // Utility function for sending a raw signed transaction to the network
    public static TransactionID submitTransaction(AlgodApi algodApiInstance, SignedTransaction signedTx ) throws Exception{
        try {
            // Msgpack encode the signed transaction
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTx);
            TransactionID id = algodApiInstance.rawTransaction(encodedTxBytes);
            return( id );
        } catch (ApiException e) {
            throw( e );
        }
    }
    public static void main(String args[]) throws Exception {
        final String ALGOD_API_ADDR = "http://localhost:8080";
        final String ALGOD_API_TOKEN = "a967f42b017cd4c5c95a633e87b5ff14226ae60609e174bf5832722631946e13";

        AlgodClient client = new AlgodClient();
        client.setBasePath(ALGOD_API_ADDR);
        ApiKeyAuth api_key = (ApiKeyAuth) client.getAuthentication("api_key");
        api_key.setApiKey(ALGOD_API_TOKEN);

        AlgodApi algodApiInstance = new AlgodApi(client);

        Account acct1 = new Account();
        Account acct2 = new Account();
        Account acct3 = new Account();
        System.out.println("Account 1 Address: " + acct1.getAddress());
        System.out.println("Account 2 Address: " + acct2.getAddress());
        System.out.println("Account 3 Address: " + acct3.getAddress());

        ChangingBlockParms cp = null;
        try {
            cp = getChangingParms(algodApiInstance);
        } catch (ApiException e) {
            e.printStackTrace();
            return;
        }        	
        try {
            cp = getChangingParms(algodApiInstance);
        } catch (ApiException e) {
            e.printStackTrace();
            return;
        }
        
        List<Ed25519PublicKey> publicKeys = new ArrayList<>();
        publicKeys.add(acct1.getEd25519PublicKey());
        publicKeys.add(acct2.getEd25519PublicKey());
        publicKeys.add(acct3.getEd25519PublicKey());
         
        MultisigAddress msig = new MultisigAddress(1, 2, publicKeys);

        final String toAddr = "WICXIYCKG672UGFCCUPBAJ7UYZ2X7GZCNBLSAPBXW7M6DZJ5YY6SCXML4A";

        Transaction tx1 = new Transaction(msig.toAddress(), new Address(toAddr), 1000, cp.firstRound.intValue(), cp.lastRound.intValue(), cp.genID, cp.genHash);

        System.out.println("Multisig Address: " + msig.toString());
        
        SignedTransaction signedTransaction = acct1.signMultisigTransaction(msig, tx1);
       
        SignedTransaction signedTrx2 = acct2.appendMultisigTransaction(msig, signedTransaction);

        System.err.println("Please go to: https://bank.testnet.algorand.network/ to fund your multisig account. \n" + msig.toAddress());
        System.in.read();

        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream( );
            byte[] encodedTxBytes = Encoder.encodeToMsgPack(signedTrx2);
            byteOutputStream.write(encodedTxBytes);

            byte stxBytes[] = byteOutputStream.toByteArray();
                        
            TransactionID id = algodApiInstance.rawTransaction(stxBytes);
            System.out.println("Successfully sent tx group with first tx id: " + id);
            } catch (ApiException e) {
                // This is generally expected, but should give us an informative error message.
                System.err.println("Exception when calling algod#rawTransaction: " + e.getResponseBody());
        }
    }
}