package com.flysfo.shorttrips.security;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.flysfo.shorttrips.R;
import com.flysfo.shorttrips.model.security.SecurityPageData;
import com.flysfo.shorttrips.networking.SfoApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecurityFragment extends Fragment {

  @BindView(R.id.webview)
  WebView webView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_security, container, false);
    ButterKnife.bind(this, view);

    SfoApi.getInstance().getSecurityPageData().enqueue(new Callback<SecurityPageData>() {
      @Override
      public void onResponse(Call<SecurityPageData> call, Response<SecurityPageData> response) {
        if (response.body() != null) {
          updateText(response.body().data);
        }
      }

      @Override
      public void onFailure(Call<SecurityPageData> call, Throwable t) {
        Answers.getInstance().logCustom(new CustomEvent(t.getMessage()));
      }
    });

    return view;
  }

  private void updateText(String text) {
    text = text.replace("’", "'");

    //noinspection ResourceType
    String hexString = getResources().getString(R.color.lot_text_blue).substring(3);

    String htmlString = "<span style=\"font-family: OpenSans; font-size: 14; " +
      "color:#" + hexString + ";\">" + text + "</span>";
    webView.loadData(htmlString, "text/html", "UTF-8");
  }
}
