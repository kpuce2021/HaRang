package com.example.harangT.rtmp;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.harangT.R;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ImageAdapter extends BaseAdapter {

  private List<ActivityLink> links;

  public ImageAdapter(List<ActivityLink> links) {
    this.links = links;
  }

  public int getCount() {
    return links.size();
  }

  public ActivityLink getItem(int position) {
    return links.get(position);
  }

  public long getItemId(int position) {
    return (long) position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView button;
    Resources resources = parent.getResources();
    float fontSize = 16;
    int paddingH = 8;
    int paddingV = 20;
    if (convertView == null) {
      button = new TextView(parent.getContext());
      button.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
      button.setTextColor(ResourcesCompat.getColor(resources, R.color.white, null));
      button.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.teal_200, null));
      button.setLayoutParams(new GridView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
      button.setPadding(paddingH, paddingV, paddingH, paddingV);
      button.setGravity(Gravity.CENTER);
      convertView = button;
    } else {
      button = (TextView) convertView;
    }
    button.setText(links.get(position).getLabel());
    return convertView;
  }
}
