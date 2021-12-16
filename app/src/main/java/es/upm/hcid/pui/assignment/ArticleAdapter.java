package es.upm.hcid.pui.assignment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import es.upm.hcid.pui.assignment.exceptions.ServerCommunicationError;

public class ArticleAdapter extends BaseAdapter implements Filterable {
    MainActivity activity;
    private List<Article> data = null;
    private List<Article> dataFiltered = null;
    private final ItemFilter itemFilter = new ItemFilter();
    private String filter;


    public ArticleAdapter(MainActivity activity) {
        this.activity = activity;
    }

    public void setData(List<Article> data) {
        this.data = data;
        this.dataFiltered = data;
        if (this.filter != null) {
            this.itemFilter.filter(this.filter);
        }

        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return dataFiltered != null ? dataFiltered.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return dataFiltered != null ? dataFiltered.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.activity_article_list, null);
        }

        Article article = dataFiltered.get(position);
        TextView articleTitleLabel = convertView.findViewById(R.id.articleTitle_text);
        articleTitleLabel.setText(article.getTitleText());

        TextView articleCategoryLabel = convertView.findViewById(R.id.articleCategory_text);
        articleCategoryLabel.setText(article.getCategory());

        TextView articleAbstractLabel = convertView.findViewById(R.id.articleAbstract_text);
        articleAbstractLabel.setMaxLines(6);
        articleAbstractLabel.setText(Html.fromHtml(article.getAbstractText(), Html.FROM_HTML_MODE_COMPACT));

        ImageView articleImageView = convertView.findViewById(R.id.articleImageView);
        Bitmap bitmap = null;
        try {
            Image img = article.getImage();
            if (img != null) {
                String str = img.getImage();
                if (str != null) {
                    bitmap = stringToBitMap(str);
                }
            }
        } catch (ServerCommunicationError serverCommunicationError) {
        }
        if (bitmap == null) {
            articleImageView.setImageResource(R.drawable.newspaper);
        } else {
            articleImageView.setImageBitmap(bitmap);
        }

        convertView.setOnClickListener(v -> {
            try {
                activity.routeToArticle(dataFiltered.get(position));
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        });

        return convertView;
    }

    public Bitmap stringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    public void setFilter(String filterer) {
        this.filter = filterer;
        this.itemFilter.filter(filterer);
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();


            final ArrayList<Article> nlist = new ArrayList<Article>(data.size());

            if (filterString.equals("all")) {
                nlist.addAll(data);
            } else {
                for (Article article : data) {
                    if (article.getCategory().toLowerCase().equals(filterString)) {
                        nlist.add(article);
                    }
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataFiltered = (List<Article>) results.values;
            notifyDataSetChanged();
        }

    }
}
