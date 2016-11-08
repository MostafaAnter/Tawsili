package com.perfect_apps.tawsili.utils;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.perfect_apps.tawsili.BuildConfig;
import com.perfect_apps.tawsili.R;
import com.perfect_apps.tawsili.app.AppController;
import com.perfect_apps.tawsili.models.DriverModel;
import com.perfect_apps.tawsili.parser.JsonParser;
import com.perfect_apps.tawsili.store.TawsiliPrefStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by mostafa_anter on 11/7/16.
 */

public class OrderDriver {

    private String mCategoryName; // category to get all drivers
    private String mCategoryValue; // category to get all drivers
    private FragmentActivity mContext; // context for read preferences
    private SweetDialogHelper sweetDialogHelper;

    private List<DriverModel> driverModels; // list that hole five drivers

    private String fromDetails;
    private String toDetails;
    private String estmiateFee;
    private String timeToCreateOrder;
    private String typeNowOrLaterValue;
    private String distanceWithKilo;
    private String orderTypeValue;
    private String promoCode;
    // for push notification to driver
    private String typeNowOrLaterName;


    public OrderDriver(FragmentActivity mContext, String mCategoryValue,
                       String mCategoryName, String fromDetails,
                       String toDetails, String estmiateFee, String timeToCreateOrder,
                       String typeNowOrLaterValue, String distanceWithKilo,
                       String orderTypeValue, String promoCode, String typeNowOrLaterName) {
        this.mContext = mContext;
        this.mCategoryValue = mCategoryValue;
        driverModels = new ArrayList<>();
        sweetDialogHelper = new SweetDialogHelper(this.mContext);

        this.mCategoryName = mCategoryName;
        this.fromDetails = fromDetails;
        this.toDetails = toDetails;
        this.estmiateFee = estmiateFee;
        this.timeToCreateOrder = timeToCreateOrder;
        this.typeNowOrLaterValue = typeNowOrLaterValue;
        this.distanceWithKilo = distanceWithKilo;
        this.orderTypeValue = orderTypeValue;
        this.promoCode = promoCode;
        this.typeNowOrLaterName = typeNowOrLaterName;


        getDriversList();
    }

    private void getDriversList() {
        String requestTag = "driversRequest";
        AppController.getInstance().getRequestQueue().cancelAll(requestTag);
        final String lat = new TawsiliPrefStore(mContext).getPreferenceValue(Constants.userLastLocationLat);
        final String lng = new TawsiliPrefStore(mContext).getPreferenceValue(Constants.userLastLocationLng);
        if (!lat.trim().isEmpty() && !lng.trim().isEmpty()) {
            sweetDialogHelper.showMaterialProgress(mContext.getString(R.string.loading));
            String url = BuildConfig.API_BASE_URL + "drivers.php?category=" + mCategoryValue + "&language=" +
                    String.valueOf(new TawsiliPrefStore(mContext)
                            .getIntPreferenceValue(Constants.PREFERENCE_DRIVER_LANGUAGE));
            StringRequest strReq = new StringRequest(Request.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Drivers", response.toString());
                    TreeSet<DriverModel> driverModelTreeSet =
                            JsonParser.parseDriversListReturnList(lat, lng, response);
                    if (driverModelTreeSet != null) {
                        // get near 5 drivers
                        Iterator<DriverModel> it = driverModelTreeSet.iterator();
                        int i = 0;
                        DriverModel current;
                        while (it.hasNext() && i < 5) {
                            current = it.next();
                            i++;
                            driverModels.add(current);
                        }

                        // create order
                        createOrder();
                    } else {
                        sweetDialogHelper.dismissDialog();
                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(mContext.getString(R.string.error))
                                .setContentText(mContext.getString(R.string.try_agin))
                                .setConfirmText(mContext.getString(R.string.yes_try_again))
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        getDriversList();
                                    }
                                })
                                .show();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error drivers", "Error: " + error.getMessage());
                    sweetDialogHelper.dismissDialog();
                    new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(mContext.getString(R.string.error))
                            .setContentText(mContext.getString(R.string.try_agin))
                            .setConfirmText(mContext.getString(R.string.yes_try_again))
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    getDriversList();
                                }
                            })
                            .show();
                }
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, requestTag);
        }

    }

    private void createOrder() {
        String url = BuildConfig.API_BASE_URL + "createorder.php";
        // here should show dialog

        JSONObject params = new JSONObject();
        try {
            params.put("client", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userId));
            params.put("passengers", "0");
            params.put("bags", "0");
            params.put("fromlat", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastLocationLat));
            params.put("fromlng", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastLocationLng));
            params.put("tolat", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastDropOffLocationLat));
            params.put("tolng", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastDropOffLocationLng));
            params.put("fromdetails", fromDetails);
            params.put("todetails", toDetails);
            params.put("estmiatefee", estmiateFee);
            params.put("status", "1");
            params.put("payment", "1");
            params.put("created", Utils.returnTime());
            params.put("time", timeToCreateOrder);
            params.put("type", typeNowOrLaterValue);
            params.put("kms", distanceWithKilo);
            params.put("ordertype", orderTypeValue);
            params.put("promocode", promoCode);


        } catch (JSONException e) {
            e.printStackTrace();
            sweetDialogHelper.dismissDialog();
        }

        CustomRequest strReq = new CustomRequest(Request.Method.POST,
                url, params, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response1) {

                String response = response1.toString();
                Log.d("create order", response.toString());

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String result = jsonObject.optString("error");
                        String orderID = jsonObject.optString("id");
                        if (!result.trim().isEmpty()) {
                            sweetDialogHelper.dismissDialog();
                            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText(mContext.getString(R.string.error))
                                    .setContentText(result + " " + mContext.getString(R.string.try_agin))
                                    .setConfirmText(mContext.getString(R.string.yes_try_again))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            getDriversList();
                                        }
                                    })
                                    .show();
                            break;
                        } else {
                            // get orderId
                            doProsess(orderID);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    sweetDialogHelper.dismissDialog();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error create order", "Error: " + error.toString());
                sweetDialogHelper.dismissDialog();
                sweetDialogHelper.showErrorMessage(mContext.getString(R.string.error),
                        mContext.getString(R.string.try_agin));
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private int driverCounterForloop = 0;
    private int processCounterLoop = 0;

    private void doProsess(String orderID) {
        String driverTokenID = "";
        switch (driverCounterForloop) {
            case 0:
                if (driverModels.size() > 0) {
                    driverTokenID = driverModels.get(0).getUdid();
                }
                break;
            case 1:
                if (driverModels.size() > 1) {
                    driverTokenID = driverModels.get(1).getUdid();
                }
                break;
            case 2:
                if (driverModels.size() > 2) {
                    driverTokenID = driverModels.get(2).getUdid();
                }
                break;
            case 3:
                if (driverModels.size() > 3) {
                    driverTokenID = driverModels.get(3).getUdid();
                }
                break;
            case 4:
                if (driverModels.size() > 4) {
                    driverTokenID = driverModels.get(4).getUdid();
                }
                break;
        }

        pushMessageToDriver(driverTokenID, orderID);
    }

    /**
     * push notification to driver
     */
    private void pushMessageToDriver(String driverTokenID, final String orderID) {
        String url = "https://gcm-http.googleapis.com/gcm/send";
        // here should show dialog

        JSONObject rootObject = new JSONObject();
        try {
            rootObject.put("to", driverTokenID);
            JSONObject dataObject = new JSONObject();
            dataObject.put("message", "Ride Request");

            JSONObject orderObject = new JSONObject();
            orderObject.put("id", orderID);
            orderObject.put("client", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userId));
            orderObject.put("fromlat", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastLocationLat));
            orderObject.put("fromlng", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastLocationLng));
            orderObject.put("fromdetail", fromDetails);
            orderObject.put("tolat", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastDropOffLocationLat));
            orderObject.put("tolng", new TawsiliPrefStore(mContext)
                    .getPreferenceValue(Constants.userLastDropOffLocationLng));
            orderObject.put("todetail", toDetails);
            orderObject.put("time", timeToCreateOrder);
            orderObject.put("type", mCategoryName);
            orderObject.put("ordertype", typeNowOrLaterName);
            dataObject.put("order", orderObject);
            rootObject.put("data", dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
            sweetDialogHelper.dismissDialog();
        }
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                url, rootObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response1) {
                String response = response1.toString();
                Log.d("create order", response.toString());

                new SleepTask().execute(orderID);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error create order", "Error: " + error.toString());
                // TODO: 11/7/16 dismiss progress
                sweetDialogHelper.dismissDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "key=AIzaSyDgJ4dFaMOMHIarKiHHtYtiPKkbwalCPi0");
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private class SleepTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(String orderId) {
            super.onPostExecute(orderId);
            checkOrder(orderId);
        }
    }

    private void checkOrder(final String orderID) {
        String url = BuildConfig.API_BASE_URL + "getorder.php?id=" + orderID;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("checkOrder", response.toString());
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String driver_id = jsonObject.optString("driver_id");
                        String status = jsonObject.optString("status");

                        if (!status.equalsIgnoreCase("Missed")) {
                            if (!driver_id.trim().equalsIgnoreCase("null")) {
                                // show success message
                                sweetDialogHelper.showSuccessfulMessage("Done!", "Your order success :)");
                            } else {
                                if (0 <= driverCounterForloop && driverCounterForloop < driverModels.size()) {
                                    driverCounterForloop++;
                                } else {
                                    if (processCounterLoop == 0) {
                                        processCounterLoop++;
                                    } else {
                                        processCounterLoop++;
                                        sweetDialogHelper.dismissDialog();
                                        // TODO: 11/8/16 return and show dialog to cancel or try again
                                        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Drivers Busy!")
                                                .setContentText("it seem our drivers are busy now")
                                                .setCancelText("Cancel")
                                                .setConfirmText("Try again!")
                                                .showCancelButton(true)
                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.cancel();
                                                        missedOrder(orderID);

                                                    }
                                                })
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismissWithAnimation();
                                                        driverCounterForloop = 0;
                                                        processCounterLoop = 0;
                                                        doProsess(orderID);
                                                    }
                                                })
                                                .show();

                                    }
                                    driverCounterForloop = 0;
                                }
                                if (processCounterLoop == 0 || processCounterLoop == 1) {
                                    doProsess(orderID);
                                }
                            }
                        } else {
                            sweetDialogHelper.dismissDialog();
                            new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Time out!")
                                    .setContentText("this order is missed if you want create new one")
                                    .setConfirmText("Ok, i know")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("checkOrder", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }


    private void missedOrder(String orderID) {
        String url = BuildConfig.API_BASE_URL + "missedorder.php?order=" + orderID;
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("missed order", response.toString());


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error missed", "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }


}
