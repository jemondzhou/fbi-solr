package cn.fbi.solrj.service;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.junit.Before;
import org.junit.Test;

import cn.fbi.solrj.pojo.Foo;
import cn.fbi.solrj.pojo.Item;

public class SolrjServiceTest
{

    private SolrjService solrjService;

    private HttpSolrServer httpSolrServer;

    @Before
    public void setUp() throws Exception
    {
        // 在url中指定core名称：taotao
        // http://solr.taotao.com/#/taotao
        String url = "http://127.0.0.1:8983/fbi";
        HttpSolrServer httpSolrServer = new HttpSolrServer(url); // 定义solr的server
        httpSolrServer.setParser(new XMLResponseParser()); // 设置响应解析器
        httpSolrServer.setMaxRetries(1); // 设置重试次数，推荐设置为1
        httpSolrServer.setConnectionTimeout(500); // 建立连接的最长时间

        this.httpSolrServer = httpSolrServer;
        solrjService = new SolrjService(httpSolrServer);
    }

    @Test
    public void testAdd() throws Exception
    {
        Foo foo = new Foo();
        foo.setId(System.currentTimeMillis());
        foo.setName("轻量级Java EE企业应用实战（第3版）：Struts2＋Spring3＋Hibernate整合开发（附CD光盘）");

        this.solrjService.add(foo);
    }

    @Test
    public void testUpdate() throws Exception
    {
        Foo foo = new Foo();
        foo.setId(1428637449061L);
        foo.setName("new - 轻量级Java EE企业应用实战（第3版）：Struts2＋Spring3＋Hibernate整合开发（附CD光盘）");

        this.solrjService.add(foo);
    }

    @Test
    public void testDelete() throws Exception
    {
        this.solrjService.delete(Arrays.asList("1428637449061"));
    }

    @Test
    public void testSearch() throws Exception
    {
        List<Item> foos = this.solrjService.search("不醉", 1, 10);
        for (Item foo : foos)
        {
            System.out.println(foo + "===============chaxun==========");

        }
    }

    @Test
    public void testDeleteByQuery() throws Exception
    {
        httpSolrServer.deleteByQuery("*:*");
        httpSolrServer.commit();
    }

}
