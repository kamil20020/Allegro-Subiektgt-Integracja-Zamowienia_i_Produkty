package pl.kamil_dywan.mapper.unit;

import org.junit.jupiter.api.Test;
import pl.kamil_dywan.external.allegro.generated.Cost;
import pl.kamil_dywan.external.allegro.generated.offer_product.*;
import pl.kamil_dywan.external.allegro.own.Country;
import pl.kamil_dywan.external.allegro.own.Currency;
import pl.kamil_dywan.external.subiektgt.own.product.Product;
import pl.kamil_dywan.external.subiektgt.own.product.ProductType;
import pl.kamil_dywan.mapper.ProductOfferMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductOfferMapperTest {

    @Test
    void shouldMap(){

        //given
        SellingMode sellingMode = new SellingMode(
            new Cost(
                new BigDecimal("34.48"),
                Currency.PLN
            )
        );

        TaxSettings taxSettings = new TaxSettings(
            List.of(
                new TaxForCountry(new BigDecimal("23"), Country.PL.toString())
            ),
            "",
            ""
        );

        ProductOffer allegroProductOffer = ProductOffer.builder()
            .id(12L)
            .name("Product offer 123")
            .sellingMode(sellingMode)
            .taxSettings(taxSettings)
            .build();

        BigDecimal expectedTaxRate = taxSettings.getTaxesFoCountries().get(0).getTaxRate();
        BigDecimal expectedUnitPriceWithTax = sellingMode.getPrice().getAmount();
        BigDecimal expectedUnitPriceWithoutTax = new BigDecimal("28.03");

        //when
        Product subiektProduct = ProductOfferMapper.map(allegroProductOffer, ProductType.GOODS);

        //then
        assertNotNull(subiektProduct);
        assertEquals(ProductType.GOODS, subiektProduct.getType());
        assertEquals(allegroProductOffer.getId().toString(), subiektProduct.getId());
        assertEquals(allegroProductOffer.getName(), subiektProduct.getName());
        assertEquals(expectedTaxRate, subiektProduct.getTaxRatePercentage());
        assertEquals(expectedUnitPriceWithoutTax, subiektProduct.getUnitPriceWithoutTax());
    }
}