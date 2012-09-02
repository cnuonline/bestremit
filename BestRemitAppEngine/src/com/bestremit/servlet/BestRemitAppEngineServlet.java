package com.bestremit.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bestremit.beans.RemitanceProviderBean;
import com.bestremit.beans.RemittanceCurrencyBean;
import com.bestremit.parser.DollarRupeeParser;
import com.bestremit.parser.RupeeParser;
@SuppressWarnings("serial")
public class BestRemitAppEngineServlet extends HttpServlet {
    
    public static Map<String,RupeeParser> symbolMaps = new HashMap<String, RupeeParser>();
    public static Map<String,String > urlMap = new HashMap<String, String>();
    public static Map<String,String > displaySymbols = new HashMap<String, String>();
    private static final Logger log = Logger.getLogger(BestRemitAppEngineServlet.class.getName()); 
     static{
         symbolMaps.put("USD", new DollarRupeeParser());
         urlMap.put("USD", "http://www.dollar2rupee.net");
         
         displaySymbols.put("USD", "\u0024");
         displaySymbols.put("EUR", "\u20AC");
         displaySymbols.put("GBP", "\u00A3");
         displaySymbols.put("Rs", "\u20A8"); //old rupee symbol
         displaySymbols.put("Rs", "\u20B9"); //new rupee symbol
         displaySymbols.put("SGD", "SG \u0024 ");
         displaySymbols.put("AUD", "AU \u0024 ");
         
     }
     public void doGet(HttpServletRequest req, HttpServletResponse resp)
             throws IOException {
         
         String symbol= req.getParameter("txtweb-message");
         log.log(Level.INFO, "Input message: "+symbol);
         RupeeParser rupeeParser = null;
         String url = null;
         String message = "";
         //Default symbol will be USD
         if (symbol != null && symbolMaps.get(symbol.trim().toUpperCase()) != null){
             rupeeParser = symbolMaps.get(symbol.trim().toUpperCase());
             url = urlMap.get(symbol.trim().toUpperCase());
             
         }
         else{
             rupeeParser = symbolMaps.get("USD");
             url = urlMap.get("USD");
             symbol = "USD";
             message = " </br><b>HowToUse:Pass the message @BestRemit (USD/EUR/GBP/).Default is US Dollars </b>";
             
         }
         try {
             List<RemitanceProviderBean> remitanceProviderBeans =  rupeeParser.parseDollar(url);
             String buildFinalMessage = buildMessage(remitanceProviderBeans,symbol);
             sendResponse(resp, buildFinalMessage+message);
         } catch (Throwable e) {
             log.log(Level.SEVERE, e.getMessage(),e);
             e.printStackTrace();
             sendResponse(resp, "oops !! some prob occured . It doesnt occur oftenly...");
         }
     }

     /*private String buildMessage(List<RemitanceProviderBean> remitanceProviderBeans,String symbol) {
        StringBuffer message = new StringBuffer("<p> Here is the best remits available in the market <p>");
         for (RemitanceProviderBean remitanceProviderBean : remitanceProviderBeans) {
             RemittanceCurrencyBean remittanceCurrencyBean = remitanceProviderBean.getRemittanceCurrencies().get(0);
             remitanceProviderBean.getCurrencySymbol();
             message.append(remitanceProviderBean.getRemittanceProviderName()).append(" provides a rate of ");
             message.append(remittanceCurrencyBean.getRate());
             message.append(" for a amount range ").append(symbol).append(" ")
                     .append(remittanceCurrencyBean.getAmount());
             message.append("</p>");
         }
        
         return message.toString();
     }
*/
     private String buildMessage(List<RemitanceProviderBean> remitanceProviderBeans,String symbol) {
         StringBuffer message = new StringBuffer("Best remits in the market for ${0-2000}. ");
         String displaySymbol = BestRemitAppEngineServlet.displaySymbols.get(symbol);
          for (RemitanceProviderBean remitanceProviderBean : remitanceProviderBeans) {
              RemittanceCurrencyBean remittanceCurrencyBean = remitanceProviderBean.getRemittanceCurrencies().get(0);
             // displaySymbol =  BestRemitAppEngineServlet.displaySymbols.get(remitanceProviderBean.getCurrencySymbol());
              message.append(remitanceProviderBean.getRemittanceProviderName()).append("->Rs ");
              message.append(remittanceCurrencyBean.getRate());
              //message.append(" ->").append(displaySymbol);//.append("{");
             // message.append(remittanceCurrencyBean.getAmount());//.append("}. ");
              message.append(". ");
          }         
          return message.toString().trim();
      }
     private void sendResponse(HttpServletResponse resp, String smsResponse) {
         try {
             PrintWriter out = resp.getWriter();
             out.println("<html><head><meta name=\"txtweb-appkey\" content=\"7c4b05c7-d4b3-48d7-b2f4-17d8d7f4eeff\" /></head><body>"
                     +smsResponse 
                     +"</body></html>"); 
         } catch (IOException e) {
             
             e.printStackTrace();
         }
         
     }

  
}
