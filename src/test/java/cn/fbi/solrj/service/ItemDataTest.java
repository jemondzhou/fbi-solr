package cn.fbi.solrj.service;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

import cn.fbi.solrj.pojo.Item;
import cn.fbi.solrj.util.EasyUIResult;

public class ItemDataTest
{
    private static final int A = 48271;

    private static final int M = 2147483647;

    private static final int Q = M / A;

    private static final int R = M % A;

    private static int state;

    public static void main(String[] args) throws Exception
    {

        String url = "http://10.0.0.102:8983/fbi";
        HttpSolrServer httpSolrServer = new HttpSolrServer(url); // 定义solr的server
        httpSolrServer.setParser(new XMLResponseParser()); // 设置响应解析器
        httpSolrServer.setMaxRetries(1); // 设置重试次数，推荐设置为1
        httpSolrServer.setConnectionTimeout(500); // 建立连接的最长时间
        // http://127.0.0.1:8081/rest/audio/getSongAll
        // getTVShowAll getMovieFilmAll getMovieTvAll audio/getSongAll
        // getStoryAll
        // 查询数据 http://127.0.0.1:8081/rest/audio/getAllPerson?page=3&rows=100
        String api = "http://127.0.0.1:8081/rest/audio/getSongAll?page={page}&rows=100";
        int page = 7517;
        int pageSize = 0;
        do
        {
            System.out.println("--------------- 当前第 " + page
                    + " 页------------------");
            String jsonData = doGet(StringUtils.replace(api, "{page}", page
                    + ""));
            EasyUIResult easyUIResult = EasyUIResult.formatToList(jsonData,
                    Item.class);
            List<Item> items = (List<Item>) easyUIResult.getRows();
            pageSize = items.size();
            for (Item item : items)
            {
                item.setId(getId());
                item.setWorktype("song");
            }
            // 将数据写入到solr中
            httpSolrServer.addBeans(items);
            httpSolrServer.commit();
            page++;
        }
        while (pageSize == 100);
    }

    private static Long getId()
    {
        Random random = new Random();
        long l = random.nextLong();
        l = System.currentTimeMillis() + l;
        state = (int) l % Integer.MAX_VALUE;
        int tmpState = A * (state % Q) - R * (state / Q);
        if (tmpState >= 0)
        {
            state = tmpState;
        }
        else
        {
            state = tmpState + M;
        }

        return (long) state;
    }

    private static String doGet(String url) throws Exception
    {
        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);

        CloseableHttpResponse response = null;
        try
        {
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200)
            {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        }
        finally
        {
            if (response != null)
            {
                response.close();
            }
            httpclient.close();
        }
        return null;
    }

}
