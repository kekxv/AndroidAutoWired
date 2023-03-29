package com.example.test_autowired.testClass.controller;


import com.kekxv.AutoWired.Controller;
import com.kekxv.AutoWired.IAutoWired;
import com.kekxv.AutoWired.RequestMapping;
import com.kekxv.AutoWired.Service;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

@Service
@Controller("/test")
public class TestController extends IAutoWired {

  @RequestMapping({"/hello"})
  public Object hello() throws JSONException {
    JSONObject json = new JSONObject();
    json.put("code", 200);
    json.put("message", "success");
    json.put("data", "");
    json.put("ok", true);
    return json;
  }
}
