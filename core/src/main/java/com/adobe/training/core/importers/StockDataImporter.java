package com.adobe.training.core.importers;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.polling.importer.ImportException;
import com.day.cq.polling.importer.Importer;
import lombok.extern.slf4j.Slf4j;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.api.SlingRepository;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@Component
@Service(Importer.class)
@Property(name = "importer.scheme", value = "stock", propertyPrivate = false)
@Slf4j
public class StockDataImporter implements Importer {
    private static final String LASTTRADE = "lastTrade";
    private static final String REQUESTDATE = "requestDate";
    private static final String REQUESTTIME = "requestTime";
    private static final String UPDOWN = "upDown";
    private static final String OPENPRICE = "openPrice";
    private static final String RANGEHIGH = "rangeHigh";
    private static final String RANGELOW = "rangeLow";
    private static final String VOLUME = "volume";
    private final String SOURCE_URL = "http://download.finance.yahoo.com/d/quotes.csv?f=sl1d1t1c1ohgv&e=.csv&s=";
    @Reference
    private SlingRepository repo;

    @Override
    public void importData(final String scheme, final String dataSource, final Resource resource)
            throws ImportException {
        try {
            // dataSource will be interpreted as the stock symbol
            URL sourceUrl = new URL(SOURCE_URL + dataSource);
            BufferedReader in = new BufferedReader(new InputStreamReader(sourceUrl.openStream()));
            String readLine = in.readLine(); // expecting only one line
            String[] lastTrade = readLine.split(",");
            log.info("Last trade for stock symbol {} was {}", dataSource, lastTrade);
            in.close();

            //persist
            writeToRepository(dataSource, lastTrade, resource);
        } catch (MalformedURLException e) {
            log.error("MalformedURLException", e);
        } catch (IOException e) {
            log.error("IOException", e);
        } catch (RepositoryException e) {
            log.error("RepositoryException", e);
        }

    }

    /**
     * Creates the Yahoo stock data structure
     * <p>
     * + <STOCK_SYMBOL> [cq:Page]
     * + lastTrade [nt:unstructured]
     * - lastTrade = <value>
     * - requestedDate = <value>
     * - requestTime = <value>
     * - upDown = <value>
     * - openPrice = <value>
     * - rangeHigh = <value>
     * - rangeLow = <value>
     * - volume = <value>
     */
    private void writeToRepository(final String stockSymbol, final String[] lastTrade, final Resource resource) throws RepositoryException {
        Session session = repo.loginService("training", null);
        Node parent = resource.adaptTo(Node.class);
        Node stockPageNode = JcrUtil.createPath(parent.getPath() + "/" + stockSymbol, "cq:Page",
                session);
        Node lastTradeNode = JcrUtil.createPath(stockPageNode.getPath() + "/lastTrade", "nt:unstructured",
                session);
        if (lastTrade.length > 8) {
            lastTradeNode.setProperty(LASTTRADE, lastTrade[1]);
            lastTradeNode.setProperty(REQUESTDATE, lastTrade[2].replace("\"", ""));
            lastTradeNode.setProperty(REQUESTTIME, lastTrade[3].replace("\"", ""));
            lastTradeNode.setProperty(UPDOWN, lastTrade[4]);
            lastTradeNode.setProperty(OPENPRICE, lastTrade[5]);
            lastTradeNode.setProperty(RANGEHIGH, lastTrade[6]);
            lastTradeNode.setProperty(RANGELOW, lastTrade[7]);
            lastTradeNode.setProperty(VOLUME, lastTrade[8]);
        }
        session.save();
        session.logout();
    }

    @Override
    public void importData(String scheme, String dataSource, Resource target,
                           String login, String password) throws ImportException {
        importData(scheme, dataSource, target);

    }
}
