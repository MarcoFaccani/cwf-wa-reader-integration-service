## WhatsApp Webhooks Documentation

* [Paylod Components](https://developers.facebook.com/docs/whatsapp/cloud-api/webhooks/components)
* [Error Codes](https://developers.facebook.com/docs/whatsapp/cloud-api/support/error-codes)
* [Debugging](https://developers.facebook.com/docs/whatsapp/cloud-api/support/troubleshooting)

> Keep in mind that whenever we as a business send a message to WA we receive several web hooks stating if the message
> has been develivered and then if it has been read

## Configuring the webhook endpoint

Keep in mind you have to configure the webhook endpoint on the
developer [facebook platform](https://developers.facebook.com/apps/1072430017003107/whatsapp-business/wa-settings/?business_id=568706634901984)
You can find the verification token on Keeper.
If you configure a new webhook don't forget to subscribe to "messages" or you won't receive any webhook