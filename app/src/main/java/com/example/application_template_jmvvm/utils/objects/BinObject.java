package com.example.application_template_jmvvm.utils.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BinObject {
    private String cardRangeStart;
    private String cardRangeEnd;
    private String ownerShip;
    private String cardType;

    public BinObject(String cardRangeStart, String cardRangeEnd, String ownerShip, String cardType) {
        this.cardRangeStart = cardRangeStart;
        this.cardRangeEnd = cardRangeEnd;
        this.ownerShip = ownerShip;
        this.cardType = cardType;
    }

    // Getters and setters can be added if needed

    public static List<BinObject> parseBins() throws JSONException {
        String binsData = "[{\"cardRangeStart\":\"1111110000000\",\"cardRangeEnd\":\"1111119999999\",\"OwnerShip\":\"ISSUER\",\"CardType\":\"C\"}," +
                "{\"cardRangeStart\":\"2222220000000\",\"cardRangeEnd\":\"2222229999999\",\"OwnerShip\":\"NONE\",\"CardType\":\"C\"}," +
                "{\"cardRangeStart\":\"3333330000000\",\"cardRangeEnd\":\"3333339999999\",\"OwnerShip\":\"BRAND\",\"CardType\":\"C\"}]";
        List<BinObject> binObjects = new ArrayList<>();
        JSONArray binsArray = new JSONArray(binsData);

        for (int i = 0; i < binsArray.length(); i++) {
            JSONObject binObjectJson = binsArray.getJSONObject(i);

            String cardRangeStart = binObjectJson.getString("cardRangeStart");
            String cardRangeEnd = binObjectJson.getString("cardRangeEnd");
            String ownerShip = binObjectJson.getString("OwnerShip");
            String cardType = binObjectJson.getString("CardType");

            BinObject binObject = new BinObject(cardRangeStart, cardRangeEnd, ownerShip, cardType);
            binObjects.add(binObject);
        }

        return binObjects;
    }

    public String getCardRangeStart() {
        return cardRangeStart;
    }

    public String getCardRangeEnd() {
        return cardRangeEnd;
    }

    public String getOwnerShip() {
        return ownerShip;
    }

    public String getCardType() {
        return cardType;
    }
}