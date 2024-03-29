package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User user1 = userRepository.save(user);
        return user1.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId) {

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        List<WebSeries> seriesList = webSeriesRepository.findAll();
        User user;
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isPresent()){
            user = userOptional.get();
        }else {
            return 0;
        }
        Integer num =0;
        SubscriptionType userSub = user.getSubscription().getSubscriptionType();


        for(WebSeries series : seriesList){
            if(series.getAgeLimit()<=user.getAge()){
                if(series.getSubscriptionType().equals(SubscriptionType.ELITE) && series.getSubscriptionType().equals(userSub)) num++;
                else if(series.getSubscriptionType().equals(SubscriptionType.PRO)){
                    if(userSub.equals(SubscriptionType.PRO) || userSub.equals(SubscriptionType.ELITE)) num++;
                }else{
                    num++;
                }
            }
        }

        return num;
    }


}
