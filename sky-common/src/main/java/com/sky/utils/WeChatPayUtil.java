package com.sky.utils;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

/**
 * 微信支付工具类
 *
 * 注意：微信支付功能已注释，当前为空实现，不执行任何实际支付逻辑。
 * 如需恢复微信支付，请从版本控制历史中恢复原始实现，并启用 pom.xml 中的
 * wechatpay-apache-httpclient 依赖以及 WeChatProperties 配置。
 */
//@Component  // 微信支付已禁用，不再作为 Spring 组件注册
public class WeChatPayUtil {

    // ===== 原微信支付相关配置与依赖（已注释）=====
    // 微信支付下单接口地址
    // public static final String JSAPI = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    // 申请退款接口地址
    // public static final String REFUNDS = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";

    // @Autowired
    // private WeChatProperties weChatProperties;

    /*
     * 原始实现：构造调用微信接口的客户端，加载商户私钥与平台证书。
     *
     * private CloseableHttpClient getClient() {
     *     PrivateKey merchantPrivateKey = null;
     *     try {
     *         merchantPrivateKey = PemUtil.loadPrivateKey(
     *             new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath())));
     *         X509Certificate x509Certificate = PemUtil.loadCertificate(
     *             new FileInputStream(new File(weChatProperties.getWeChatPayCertFilePath())));
     *         List<X509Certificate> wechatPayCertificates = Arrays.asList(x509Certificate);
     *         WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
     *             .withMerchant(weChatProperties.getMchid(),
     *                 weChatProperties.getMchSerialNo(), merchantPrivateKey)
     *             .withWechatPay(wechatPayCertificates);
     *         CloseableHttpClient httpClient = builder.build();
     *         return httpClient;
     *     } catch (FileNotFoundException e) {
     *         e.printStackTrace();
     *         return null;
     *     }
     * }
     */

    /*
     * 原始实现：发送 post 方式请求。
     *
     * private String post(String url, String body) throws Exception {
     *     CloseableHttpClient httpClient = getClient();
     *     HttpPost httpPost = new HttpPost(url);
     *     httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
     *     httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
     *     httpPost.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
     *     httpPost.setEntity(new StringEntity(body, "UTF-8"));
     *     CloseableHttpResponse response = httpClient.execute(httpPost);
     *     try {
     *         String bodyAsString = EntityUtils.toString(response.getEntity());
     *         return bodyAsString;
     *     } finally {
     *         httpClient.close();
     *         response.close();
     *     }
     * }
     */

    /*
     * 原始实现：发送 get 方式请求。
     *
     * private String get(String url) throws Exception {
     *     CloseableHttpClient httpClient = getClient();
     *     HttpGet httpGet = new HttpGet(url);
     *     httpGet.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
     *     httpGet.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
     *     httpGet.addHeader("Wechatpay-Serial", weChatProperties.getMchSerialNo());
     *     CloseableHttpResponse response = httpClient.execute(httpGet);
     *     try {
     *         String bodyAsString = EntityUtils.toString(response.getEntity());
     *         return bodyAsString;
     *     } finally {
     *         httpClient.close();
     *         response.close();
     *     }
     * }
     */

    /*
     * 原始实现：jsapi 下单。
     *
     * private String jsapi(String orderNum, BigDecimal total, String description, String openid) throws Exception {
     *     JSONObject jsonObject = new JSONObject();
     *     jsonObject.put("appid", weChatProperties.getAppid());
     *     jsonObject.put("mchid", weChatProperties.getMchid());
     *     jsonObject.put("description", description);
     *     jsonObject.put("out_trade_no", orderNum);
     *     jsonObject.put("notify_url", weChatProperties.getNotifyUrl());
     *     JSONObject amount = new JSONObject();
     *     amount.put("total", total.multiply(new BigDecimal(100))
     *         .setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
     *     amount.put("currency", "CNY");
     *     jsonObject.put("amount", amount);
     *     JSONObject payer = new JSONObject();
     *     payer.put("openid", openid);
     *     jsonObject.put("payer", payer);
     *     String body = jsonObject.toJSONString();
     *     return post(JSAPI, body);
     * }
     */

    /**
     * 小程序支付（空实现，不执行任何实际支付逻辑）。
     *
     * @param orderNum    商户订单号
     * @param total       金额，单位 元
     * @param description 商品描述
     * @param openid      微信用户的openid
     * @return 空的 JSONObject
     */
    public JSONObject pay(String orderNum, BigDecimal total, String description, String openid) throws Exception {
        // 微信支付功能已禁用，什么都不执行，直接返回空对象
        /*
        //统一下单，生成预支付交易单
        String bodyAsString = jsapi(orderNum, total, description, openid);
        //解析返回结果
        JSONObject jsonObject = JSON.parseObject(bodyAsString);
        System.out.println(jsonObject);

        String prepayId = jsonObject.getString("prepay_id");
        if (prepayId != null) {
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            String nonceStr = RandomStringUtils.randomNumeric(32);
            ArrayList<Object> list = new ArrayList<>();
            list.add(weChatProperties.getAppid());
            list.add(timeStamp);
            list.add(nonceStr);
            list.add("prepay_id=" + prepayId);
            //二次签名，调起支付需要重新签名
            StringBuilder stringBuilder = new StringBuilder();
            for (Object o : list) {
                stringBuilder.append(o).append("\n");
            }
            String signMessage = stringBuilder.toString();
            byte[] message = signMessage.getBytes();

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(PemUtil.loadPrivateKey(
                new FileInputStream(new File(weChatProperties.getPrivateKeyFilePath()))));
            signature.update(message);
            String packageSign = Base64.getEncoder().encodeToString(signature.sign());

            //构造数据给微信小程序，用于调起微信支付
            JSONObject jo = new JSONObject();
            jo.put("timeStamp", timeStamp);
            jo.put("nonceStr", nonceStr);
            jo.put("package", "prepay_id=" + prepayId);
            jo.put("signType", "RSA");
            jo.put("paySign", packageSign);

            return jo;
        }
        return jsonObject;
        */
        return new JSONObject();
    }

    /**
     * 申请退款（空实现，不执行任何实际退款逻辑）。
     *
     * @param outTradeNo    商户订单号
     * @param outRefundNo   商户退款单号
     * @param refund        退款金额
     * @param total         原订单金额
     * @return 空字符串
     */
    public String refund(String outTradeNo, String outRefundNo, BigDecimal refund, BigDecimal total) throws Exception {
        // 微信退款功能已禁用，什么都不执行，直接返回空字符串
        /*
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", outTradeNo);
        jsonObject.put("out_refund_no", outRefundNo);

        JSONObject amount = new JSONObject();
        amount.put("refund", refund.multiply(new BigDecimal(100))
            .setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("total", total.multiply(new BigDecimal(100))
            .setScale(2, BigDecimal.ROUND_HALF_UP).intValue());
        amount.put("currency", "CNY");

        jsonObject.put("amount", amount);
        jsonObject.put("notify_url", weChatProperties.getRefundNotifyUrl());

        String body = jsonObject.toJSONString();

        //调用申请退款接口
        return post(REFUNDS, body);
        */
        return "";
    }
}
