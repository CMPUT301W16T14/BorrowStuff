package t14.com.GameRentals;

import java.util.ArrayList;

/**
 * Created by cjresler on 2016-02-28.
 */
public class BidList {
    private ArrayList<Bid> list;

    public BidList() {
        list = new ArrayList<Bid>();
    }

    public ArrayList<Bid> getList() {
        return list;
    }
    public int getSize(){
        return list.size();
    }

    public Bid getItem(int position){
        Bid bid;
        bid = list.get(position);
        return bid;
    }

    public void AddBid(User user, double rate){
        Bid bid = new Bid(user,rate);
        list.add(bid);
    }

    public void RemoveBid(Game game){
        list.remove(game);
    }
}
