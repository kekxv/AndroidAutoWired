package com.kekxv.AutoWired;


import fi.iki.elonen.NanoHTTPD;

public interface IFilterHandler {
  default NanoHTTPD.Response onRequest(NanoHTTPD.IHTTPSession session) {
    return null;
  }

  default NanoHTTPD.Response onResponse(NanoHTTPD.IHTTPSession session, NanoHTTPD.Response response) {
    return response;
  }
}
