package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        Optional<User> userOptional= userRepository.findById(subscriptionEntryDto.getUserId());
        User user;
        if(userOptional.isPresent()){
            user = userOptional.get();
        }
        else {
            return 0;
        }

        subscription.setStartSubscriptionDate(new Date());

        int s = subscriptionEntryDto.getNoOfScreensRequired();

        Integer price;

        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            price = 500 +(s*200);
        }else if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)) {
            price = 800 + (s * 250);
        }else price = 1000+(s*350);

        subscription.setTotalAmountPaid(price);

        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);

        return price;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        Optional<User> userOptional= userRepository.findById(userId);
        User user;
        if(userOptional.isPresent()){
            user = userOptional.get();
        }
        else {
            return 0;
        }

        Subscription subscription = user.getSubscription();

        int oldPrice;
        int newPrice;


        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)) throw new Exception("Already the best Subscription");
        else if (subscription.getSubscriptionType().equals(SubscriptionType.PRO)){

            newPrice = 1000+(350*subscription.getNoOfScreensSubscribed());
            oldPrice = subscription.getTotalAmountPaid();


            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(newPrice);

            userRepository.save(user);
        }else{
            newPrice = 800+(250*subscription.getNoOfScreensSubscribed());
            oldPrice = subscription.getTotalAmountPaid();

            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(newPrice);

            userRepository.save(user);
        }

        return newPrice-oldPrice;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subList = subscriptionRepository.findAll();
        int revenue=0;
        for(Subscription subscription:subList){
            revenue+=subscription.getTotalAmountPaid();
        }
        return revenue;
    }

}
