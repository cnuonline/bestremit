
package com.bestremit.beans;

/**
 * RemittanceCurrencyBean
 *
 * @author skoppa
 * @version $Id$
 */
public class RemittanceCurrencyBean {

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    private String amount;
    private String rate;
    
    @Override
    public String toString() {
       StringBuffer sb = new StringBuffer();
       sb.append("Amount : ").append(amount);
       sb.append("rate : ").append(rate);
       
        return sb.toString();
    }
}
