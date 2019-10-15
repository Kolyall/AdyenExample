package com.github.adyenexample.ext

import android.content.Context
import com.adyen.checkout.redirect.RedirectComponent
import com.github.adyenexample.BuildConfig
import com.github.kolyall.adyen.model.ApiAdditionalData
import com.github.kolyall.adyen.model.ApiAmount
import com.github.kolyall.adyen.model.ApiPaymentMethodDetails
import com.github.kolyall.adyen.model.ApiPaymentMethodsRequest
import com.github.kolyall.adyen.model.ApiPaymentsRequest

private const val DEFAULT_COUNTRY = "NL"
private const val DEFAULT_LOCALE = "en_US"
private const val DEFAULT_VALUE = 1337
private const val DEFAULT_CURRENCY = "EUR"

fun createPaymentMethodsRequest(shopperReferenceId: String): ApiPaymentMethodsRequest {
    return ApiPaymentMethodsRequest(
        merchantAccount = BuildConfig.MERCHANT_ACCOUNT,
        shopperReference = shopperReferenceId, amount = getAmount(),
        countryCode = DEFAULT_COUNTRY,
        shopperLocale = DEFAULT_LOCALE
    )
}

fun createPaymentsRequest(context: Context,
                          paymentMethod: ApiPaymentMethodDetails,
                          storePaymentMethod: Boolean,
                          shopperReferenceId: String): ApiPaymentsRequest {

    return ApiPaymentsRequest(
        paymentMethod = paymentMethod,
        shopperReference = shopperReferenceId,
        storePaymentMethod = storePaymentMethod,
        amount = getAmount(),
        merchantAccount = BuildConfig.MERCHANT_ACCOUNT,
        returnUrl = RedirectComponent.getReturnUrl(context),
        additionalData = ApiAdditionalData()
    )
}

fun getAmount(): ApiAmount {
    return ApiAmount().apply {
        currency = DEFAULT_CURRENCY
        value = DEFAULT_VALUE
    }
}

/*
curl -X POST "https://checkout-test.adyen.com/v49/paymentMethods" -H "x-API-key: AQEnhmfuXNWTK0Qc+iSem2czssWYS4RYA4e1EkGpAdZVGKDGKy1kfMzcEMFdWw2+5HzctViMSCJMYAc=-THbZKZYRD9XCtjwnM8nOqIRK4WE/sDvXQvxchuctzLI=-d57P5QUDnvq8Af89" -H "Content-Type: application/json"  -d '{"amount":{"currency":"EUR","value":1337},"channel":"android","countryCode":"NL","merchantAccount":"NickuAccountECOM","shopperLocale":"en_US","shopperReference":"shopperReferenceId"}' -L

curl -X POST "https://checkout-test.adyen.com/v49/payments" -H "x-API-key: AQEnhmfuXNWTK0Qc+iSem2czssWYS4RYA4e1EkGpAdZVGKDGKy1kfMzcEMFdWw2+5HzctViMSCJMYAc=-THbZKZYRD9XCtjwnM8nOqIRK4WE/sDvXQvxchuctzLI=-d57P5QUDnvq8Af89" -H "Content-Type: application/json"  -d '{"additionalData":{"allow3DS2":"false"},"amount":{"currency":"EUR","value":1337},"channel":"android","merchantAccount":"NickuAccountECOM","paymentMethod":{"encryptedCardNumber":"adyenan0_1_1$mxFty2Ina7uGupD84O8kY7pNu6eZAJhHsk4RZI5txISgUCutljve3yBzuszug0DinhyOgvS2E5oybpzCSIed4DWiNNguB5fGtLrxsrxrKpVxQr4g97n/A9MweRXI/ivMvGRQT+MDlXvt1mFWr0YE8HyQcXLrbC8cb1xmiqQoRBdt8pn6JkKJvNiSYngsMhhyHVjAxFaTO6zpwPHHSdnNSg9D0HmFoqg+5XwBH3o4wGK8dXOcJKxzZ/O4eu8N3P+0bG9B4+isCBNcZbKIDS0yKjwgNoiOHwEeVjFmKfN7VXnkPhNOGGOVPfHRimGkmSscF8ujIyhnQ3Lq9n6v/snXtQ==$Y4Nzsv4diiOh12awLKm5oaPmFArnuhae9oEG9+hxUUB5DVrG9OsrxN8QPDiFSpN8GH3S5M6OTdoHBe8+mBOGuJTA8h+quXbyENdsiyR1GxZfbgABRKmtHu855WcN","encryptedExpiryMonth":"adyenan0_1_1$tZ+ANxsHj5G6gLRrnzwfcjKJ/p4gOgA0d9lOFWj6OWMpYzA5JuY78y/lgXxvo/bOL7V/tzzbLULsbWnPC7qj1wzF37ttyllnkrPrmGRpuVN93N+7mOfpBeUmgUfENQGT4LFALkax7wcdfr1yNnhh1f7jgmoPGJsygS/ba45mTNv5+Sa1VqYseTzyj3oBDoGNd9qH1s/Uu1VtmPcvCc+UUJ/MElCvbTOVlTyulWWVaAEXFsAhABy1BiT5nfKSQUanTCA7kvLmp0AdXmYTJyBX3QBIlT7jctN8kEGWnyi69hOOadCfMJ1jD4KQt1eBzgg0YLrYH2FmevQMwOfr3cZbdA==$1GWDn8Cm/OClrAWTQz/W92V72nmYXS/SY1APdpIs973kkMyBlQKqdRdEJUU/AjawbOcz5Gt5yg7xCxiMGt+TkbvKsCg9/9fXs5LtrRdG1eI025F3","encryptedExpiryYear":"adyenan0_1_1$fAwmDH3HRT1TM3mO6S9EEME4sCTH6rVkxzIjS8th6l/ycxn9bIvhyZsdWSeQmu5Fy7TidX2jn7P0177/K6J5TrNUBlNyPT7Boi8kyPoTLezvAdZaWNxsYoJLQua0m20+Z3K7pRSJ1fStGw7SVrowWhHEIMgV1/xYfUrBVVVdA+fD300Dndw8jrqSAPBPJzZeYEqkldiuKdvL7rwsJiSG0OTzyiq7Tlc5fsiP5l65fTNwogRFw3MxS2uXtNMwkh6Dfk7DY6VxM3xXpSPM71g4vegMcWu0omgPf0f2U5WJ0jcVWyOiFoPOIm4jfEDZ0h+JH13ld0aUePRrR3p1WGT7sA==$Y8F//9aTMb1JLVf/5vru0/ZLot0odoU/jMcs808en43beLTroY6Ll45encoOc5W/a7XVAOl0ZKnwFhfkIl36jm2DK6bSSF21ZPuYpzJsmqfdItDbXA==","encryptedSecurityCode":"adyenan0_1_1$0WrX4iMbaOmrrohoENgXiogCUZ1IXkqgz+yKuuKVNN0h+VmjhqlucpvlPJ6xCjdGz+R/inYdzUw0/B7hNg0dy+RBCfkt4v/7FF9W5WuMwetLc0B+EVFthJm0YOHE3OaogGtOnOHEq94HbO5/XS2W5R5HVFrxD2wDf6S2Sotm0HoQsvQ8HPn+59AkQwQdkYVQEl30O0AYnLvkFgEF2r/3N3v8uaG51L+KTZLMC8XdWUK33Yol/KFm3JEOO8dLuLbeeGv6PDyOGRSSnrYbf9u55XQfqxI/5U3N2MgGa8A8/Tras21QqXwdfk77UCo4pRjlZ2yDZmFrBQp5nJ12DOvDYw==$lZn3ZGDVuCM79WK3E28BExtWzojYfN0ZB0aLFFsI6+BHxD7BRMW645ZCMea9r1530DHU8zXUFOSdBjyXfhA6PgammTXZcmuaxpDENUs=","holderName":"gsge","type":"scheme"},"reference":"android-test-components","returnUrl":"adyencheckout://com.github.adyenexample","shopperReference":"shopperReferenceId","storePaymentMethod":true}' -L

* */

/*

{
   "additionalData": {
      "recurringProcessingModel": "CardOnFile",
      "recurring.recurringDetailReference": "8415710463615192",
      "recurring.shopperReference": "shopperReferenceId"
   },
   "pspReference": "852571047007114H",
   "resultCode": "Authorised",
   "merchantReference": "android-test-components"
}

*/