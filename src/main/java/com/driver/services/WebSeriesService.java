package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import jdk.tools.jlink.internal.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        Optional<ProductionHouse> productionHouseOptional = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());

        ProductionHouse productionHouse;

        if(productionHouseOptional.isPresent()){
          productionHouse = productionHouseOptional.get();
        }else{
            throw  new Exception("Prodcution House does not exist");
        }


        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName()
                ,webSeriesEntryDto.getAgeLimit(),webSeriesEntryDto.getRating()
                ,webSeriesEntryDto.getSubscriptionType());

        webSeries.setProductionHouse(productionHouse);

        if (productionHouse.getWebSeriesList().contains(webSeries)) throw new Exception("Series is already present");
        else{

            double rating = (productionHouse.getRatings()*productionHouse.getWebSeriesList().size());
            productionHouse.setRatings((rating+webSeries.getRating())/productionHouse.getWebSeriesList().size()+1);
            productionHouse.getWebSeriesList().add(webSeries);
            productionHouseRepository.save(productionHouse);
        }
        return null;
    }

}
