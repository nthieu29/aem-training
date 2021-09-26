package com.adobe.training.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Model(adaptables = Resource.class)
public class StockModel {
    @Inject
    @Self
    private Node stock;

    @Inject
    private Node lastTrade;

    public String getStockSymbol() throws RepositoryException {
        return stock.getName();
    }

    public double getLastTrade() throws Exception {
        return Double.parseDouble(lastTrade.getProperty("lastTrade").getString());
    }

    public String getRequestDate() throws Exception {
        return lastTrade.getProperty("requestDate").getString();

    }

    public String getRequestTime() throws Exception {
        return lastTrade.getProperty("requestTime").getString();

    }

    public String getTimestamp() throws Exception {
        return getRequestDate() + " " + getRequestTime();
    }

    public double getUpDown() throws Exception {
        return Double.parseDouble(lastTrade.getProperty("upDown").getString());
    }

    public double getOpenPrice() throws Exception {
        return Double.parseDouble(lastTrade.getProperty("openPrice").getString());
    }

    public double getRangeHigh() throws Exception {
        return Double.parseDouble(lastTrade.getProperty("rangeHigh").getString());
    }

    public double getRangeLow() throws Exception {
        return Double.parseDouble(lastTrade.getProperty("rangeLow").getString());
    }

    public int getVolume() throws Exception {
        return Integer.parseInt(lastTrade.getProperty("volume").getString());
    }
}
