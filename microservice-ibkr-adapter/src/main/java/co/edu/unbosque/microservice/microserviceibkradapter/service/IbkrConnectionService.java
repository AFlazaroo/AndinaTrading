package co.edu.unbosque.microservice.microserviceibkradapter.service;

import com.ib.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class IbkrConnectionService implements EWrapper {

    private static final Logger logger = LoggerFactory.getLogger(IbkrConnectionService.class);

    private final EClientSocket client = new EClientSocket(this, new EJavaSignal());
    private final EReader reader;
    private final AtomicInteger nextOrderId = new AtomicInteger();

    // Almacenamiento simple para precios de mercado (tickerId -> price)
    private final Map<Integer, Double> marketDataStore = new ConcurrentHashMap<>();

    @Value("${ibkr.api.host}")
    private String host;

    @Value("${ibkr.api.port}")
    private int port;

    @Value("${ibkr.api.clientId}")
    private int clientId;

    public IbkrConnectionService() {
        this.reader = new EReader(client, new EJavaSignal());
        reader.start(); // Inicia el hilo lector de mensajes
    }

    @PostConstruct
    private void connect() {
        logger.info("Connecting to TWS on {}:{} with clientId {}", host, port, clientId);
        client.eConnect(host, port, clientId);

        // Hilo para procesar mensajes entrantes de la API de IBKR
        new Thread(() -> {
            while (client.isConnected()) {
                try {
                    reader.processMsgs();
                } catch (Exception e) {
                    logger.error("Error processing TWS messages: {}", e.getMessage());
                }
            }
        }).start();
    }

    @PreDestroy
    private void disconnect() {
        logger.info("Disconnecting from TWS...");
        client.eDisconnect();
    }

    public Map<Integer, Double> getMarketDataStore() {
        return this.marketDataStore;
    }

    /**
     * Solicita datos de mercado para un símbolo.
     * @param tickerId Un ID único para esta solicitud.
     * @param symbol El símbolo del activo (ej: "AAPL").
     */
    public void requestMarketData(int tickerId, String symbol) {
        if (!client.isConnected()) {
            logger.error("Not connected to TWS. Cannot request market data.");
            return;
        }
        Contract contract = new Contract();
        contract.symbol(symbol);
        contract.secType("STK");
        contract.exchange("SMART");
        contract.currency("USD");

        // El "false" al final es para no solicitar datos de 'snapshot' sino streaming
        client.reqMktData(tickerId, contract, "", false, false, null);
        logger.info("Requested market data for symbol {} with tickerId {}", symbol, tickerId);
    }

    public void cancelMarketData(int tickerId) {
        if (client.isConnected()) {
            client.cancelMktData(tickerId);
            marketDataStore.remove(tickerId);
            logger.info("Canceled market data for tickerId {}", tickerId);
        }
    }


    // --- Métodos de la interfaz EWrapper ---

    @Override
    public void nextValidId(int orderId) {
        // Se llama cuando la conexión es exitosa y estamos listos para operar
        logger.info("Connection to TWS successful. Next valid orderId: {}", orderId);
        this.nextOrderId.set(orderId);
    }

    @Override
    public void error(Exception e) {
        logger.error("TWS API Error: ", e);
    }

    @Override
    public void error(String str) {
        logger.error("TWS API Error: {}", str);
    }

    @Override
    public void error(int id, int errorCode, String errorMsg, String advancedOrderRejectJson) {
        if (id == -1) { // -1 indica notificaciones, no errores graves
            logger.info("TWS Notification. Code: {}, Msg: {}", errorCode, errorMsg);
        } else {
            logger.error("TWS API Error. Id: {}, Code: {}, Msg: {}", id, errorCode, errorMsg);
        }
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, TickAttrib attrib) {
        // TickType.LAST (campo 4) es el último precio de transacción
        if (field == TickType.LAST.index()) {
            logger.info("Received tick price for tickerId {}: {}", tickerId, price);
            marketDataStore.put(tickerId, price);
        }
    }

    // --- Resto de métodos de EWrapper (implementaciones vacías para este PoC) ---
    @Override
    public void tickSize(int tickerId, int field, EDecimal size) {}
    @Override
    public void tickOptionComputation(int tickerId, int field, int tickAttrib, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {}
    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {}
    @Override
    public void tickString(int tickerId, int tickType, String value) {}
    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureLastTradeDate, double dividendImpact, double dividendsToLastTradeDate) {}
    @Override
    public void orderStatus(int orderId, String status, EDecimal filled, EDecimal remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld, double mktCapPrice) {}
    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {}
    @Override
    public void openOrderEnd() {}
    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {}
    @Override
    public void updatePortfolio(Contract contract, EDecimal position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {}
    @Override
    public void updateAccountTime(String timeStamp) {}
    @Override
    public void accountDownloadEnd(String accountName) {}
    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {}
    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {}
    @Override
    public void contractDetailsEnd(int reqId) {}
    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {}
    @Override
    public void execDetailsEnd(int reqId) {}
    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, EDecimal size) {}
    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, EDecimal size, boolean isSmartDepth) {}
    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {}
    @Override
    public void managedAccounts(String accountsList) {}
    @Override
    public void receiveFA(int faDataType, String xml) {}
    @Override
    public void historicalData(int reqId, Bar bar) {}
    @Override
    public void scannerParameters(String xml) {}
    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {}
    @Override
    public void scannerDataEnd(int reqId) {}
    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, EDecimal volume, EDecimal wap, int count) {}
    @Override
    public void currentTime(long time) {}
    @Override
    public void fundamentalData(int reqId, String data) {}
    @Override
    public void deltaNeutralValidation(int reqId, DeltaNeutralContract deltaNeutralContract) {}
    @Override
    public void tickSnapshotEnd(int reqId) {}
    @Override
    public void marketDataType(int reqId, int marketDataType) {}
    @Override
    public void commissionReport(CommissionReport commissionReport) {}
    @Override
    public void position(String account, Contract contract, EDecimal pos, double avgCost) {}
    @Override
    public void positionEnd() {}
    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {}
    @Override
    public void accountSummaryEnd(int reqId) {}
    @Override
    public void verifyMessageAPI(String apiData) {}
    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {}
    @Override
    public void verifyAndAuthMessageAPI(String apiData, String xyzChallenge) {}
    @Override
    public void verifyAndAuthCompleted(boolean isSuccessful, String errorText) {}
    @Override
    public void displayGroupList(int reqId, String groups) {}
    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {}
    @Override
    public void connectAck() {}
    @Override
    public void positionMulti(int reqId, String account, String modelCode, Contract contract, EDecimal pos, double avgCost) {}
    @Override
    public void positionMultiEnd(int reqId) {}
    @Override
    public void accountUpdateMulti(int reqId, String account, String modelCode, String key, String value, String currency) {}
    @Override
    public void accountUpdateMultiEnd(int reqId) {}
    @Override
    public void securityDefinitionOptionalParameter(int reqId, String exchange, int underlyingConId, String tradingClass, String multiplier, Set<String> expirations, Set<Double> strikes) {}
    @Override
    public void securityDefinitionOptionalParameterEnd(int reqId) {}
    @Override
    public void softDollarTiers(int reqId, SoftDollarTier[] tiers) {}
    @Override
    public void familyCodes(FamilyCode[] familyCodes) {}
    @Override
    public void symbolSamples(int reqId, ContractDescription[] contractDescriptions) {}
    @Override
    public void historicalDataEnd(int reqId, String startDateStr, String endDateStr) {}
    @Override
    public void mktDepthExchanges(DepthMktDataDescription[] depthMktDataDescriptions) {}
    @Override
    public void tickNews(int tickerId, long timeStamp, String providerCode, String articleId, String headline, String extraData) {}
    @Override
    public void smartComponents(int reqId, Map<Integer, Map.Entry<String, Character>> theMap) {}
    @Override
    public void tickReqParams(int tickerId, double minTick, String bboExchange, int snapshotPermissions) {}
    @Override
    public void newsProviders(NewsProvider[] newsProviders) {}
    @Override
    public void newsArticle(int requestId, int articleType, String articleText) {}
    @Override
    public void historicalNews(int requestId, String time, String providerCode, String articleId, String headline) {}
    @Override
    public void historicalNewsEnd(int requestId, boolean hasMore) {}
    @Override
    public void headTimestamp(int reqId, String headTimestamp) {}
    @Override
    public void histogramData(int reqId, List<HistogramEntry> items) {}
    @Override
    public void historicalDataUpdate(int reqId, Bar bar) {}
    @Override
    public void rerouteMktDataReq(int reqId, int conId, String exchange) {}
    @Override
    public void rerouteMktDepthReq(int reqId, int conId, String exchange) {}
    @Override
    public void marketRule(int marketRuleId, PriceIncrement[] priceIncrements) {}
    @Override
    public void pnl(int reqId, double dailyPnL, double unrealizedPnL, double realizedPnL) {}
    @Override
    public void pnlSingle(int reqId, EDecimal pos, double dailyPnL, double unrealizedPnL, double realizedPnL, double value) {}
    @Override
    public void historicalTicks(int reqId, List<HistoricalTick> ticks, boolean done) {}
    @Override
    public void historicalTicksBidAsk(int reqId, List<HistoricalTickBidAsk> ticks, boolean done) {}
    @Override
    public void historicalTicksLast(int reqId, List<HistoricalTickLast> ticks, boolean done) {}
    @Override
    public void tickByTickAllLast(int reqId, int tickType, long time, double price, EDecimal size, TickAttribLast attribs, String exchange, String specialConditions) {}
    @Override
    public void tickByTickBidAsk(int reqId, long time, double bidPrice, double askPrice, EDecimal bidSize, EDecimal askSize, TickAttribBidAsk attribs) {}
    @Override
    public void tickByTickMidPoint(int reqId, long time, double midPoint) {}
    @Override
    public void orderBound(long orderId, int apiClientId, int apiOrderId) {}
    @Override
    public void completedOrder(Contract contract, Order order, OrderState orderState) {}
    @Override
    public void completedOrdersEnd() {}
    @Override
    public void replaceFAEnd(int reqId, String text) {}
    @Override
    public void wshMetaData(int reqId, String dataJson) {}
    @Override
    public void wshEventData(int reqId, String dataJson) {}
    @Override
    public void historicalSchedule(int reqId, String startDateTime, String endDateTime, String timeZone, List<HistoricalSession> sessions) {}
    @Override
    public void userInfo(int reqId, String whiteBrandingId) {}
}