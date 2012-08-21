
package com.bestremit.parser;

import java.util.List;

import com.bestremit.beans.RemitanceProviderBean;
import com.bestremit.exceptions.BestRemitException;

/**
 * RupeeParser
 *
 * @author skoppa
 * @version $Id$
 */
public abstract class  RupeeParser {
    public abstract List<RemitanceProviderBean> parseDollar(String htmlContent) throws BestRemitException;
}
