package com.kekxv.AutoWired;


import fi.iki.elonen.NanoHTTPD;

public interface IErrorHandler {
  NanoHTTPD.Response handler(NanoHTTPD.IHTTPSession session, Throwable e);
}
