package atm;

import common.CurrencyType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CassetteFactoryTest {

    @Test
    void getNew() throws Exception {
        Cassette cassette = CassetteFactory.getNew(CurrencyType.ROUBLE, 11, 10);
        assertEquals(CurrencyType.ROUBLE, cassette.getCurrencyType());
        assertEquals(11, cassette.getNominal());
        assertEquals(10, cassette.getCapacity());

        cassette = CassetteFactory.getNew(CurrencyType.DOLLAR, 22, 20);
        assertEquals(CurrencyType.DOLLAR, cassette.getCurrencyType());
        assertEquals(22, cassette.getNominal());
        assertEquals(20, cassette.getCapacity());

        cassette = CassetteFactory.getNew(CurrencyType.EURO, 33, 30);
        assertEquals(CurrencyType.EURO, cassette.getCurrencyType());
        assertEquals(33, cassette.getNominal());
        assertEquals(30, cassette.getCapacity());
    }

    @Test
    void getNewWithWrongCapacity() throws Exception {
        assertThrows(Exception.class, () -> CassetteFactory.getNew(CurrencyType.ROUBLE, 10, 50));
    }
}