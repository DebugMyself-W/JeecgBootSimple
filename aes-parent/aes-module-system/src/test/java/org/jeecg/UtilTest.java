package org.jeecg;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class UtilTest {

    @Test
    public void roundNumTest() {
        Double qty = 6.5d;
        Integer roundNumber = 5;
        if (roundNumber != null) {
            if(qty/roundNumber < 1) {
                qty = Double.valueOf(roundNumber + "");
            } else {
                qty = Double.valueOf(roundNumber * Math.round(qty/roundNumber));
            }
        }
        log.info("====" + qty);
    }
}
