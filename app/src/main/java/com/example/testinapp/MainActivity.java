package com.example.testinapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BillingClient billingClient;
    List<ProductDetails> productDetails;
    ProductDetails prD;
    PurchasesUpdatedListener purchasesUpdatedListener;
    Purchase purchase;
    BillingResult billingResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null){
                    for(Purchase purchase : list){
                        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()){

                            handlePurchase(purchase);
                        }
                    }
                }

            }
        };

        billingClient = BillingClient.newBuilder(MainActivity.this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGoogle();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    getProductDetails();

                }
            }
        });

        connectGoogle();
        getProductDetails();
        
    }



    private void connectGoogle(){

    }

    private void getProductDetails(){
        QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(
                ImmutableList.of(QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("free_image_animal_15_day")
                        .setProductType(BillingClient.ProductType.INAPP).build())
                ).build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams, new ProductDetailsResponseListener() {
                    @Override
                    public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                            Log.d("test","adisuhbdau");
                            if (!list.isEmpty()) {
                                TextView title = findViewById(R.id.title1);
                                TextView price = findViewById(R.id.price1);
                                Button btn_buy = findViewById(R.id.btn_buy);
                                prD = list.get(0);
                                title.setText(prD.getName());
                                price.setText((prD.getOneTimePurchaseOfferDetails().getFormattedPrice()));
                            btn_buy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                                            ImmutableList.of(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                            .setProductDetails(prD)
                                                            .build()
                                            );
                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setProductDetailsParamsList(productDetailsParamsList)
                                            .build();

//                                    activity.setIntent(new Intent());
                                    billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
                               }
                            });
                                }
//                        }else {
//                                Log.d("Test", "ten" + prD.getName());
//                            }
                    }
                }

        );
    }
    void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
    }


}