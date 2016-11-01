package net.aimeizi.service.impl;

import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import net.aimeizi.model.Farm;
import net.aimeizi.service.FarmService;
import net.aimeizi.service.Services;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class FarmServiceImpl implements FarmService {


    /**
     * 检索
     *
     * @param field
     * @param queryString
     * @param older
     * @param pageNumber
     * @param pageSize
     * @return
     * @throws Exception
     */
    public Map<String, Object> search(String field, String queryString, String older, int pageNumber, int pageSize) throws Exception {
        List<Farm> farms = new ArrayList<Farm>();
        JestClient jestClient = Services.getJestClient();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建查询
        if ("all".equals(field)) {
            searchSourceBuilder.query(QueryBuilders.queryStringQuery(queryString));
        } else {
            searchSourceBuilder.query(QueryBuilders.termQuery(field, queryString));
            // 设置排序
            searchSourceBuilder.sort(field, "asc".equals(older) ? SortOrder.ASC : SortOrder.DESC); // 设置排序字段及排序顺序
        }
        // 设置高亮字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("province");
        highlightBuilder.field("market");
        highlightBuilder.field("type");
        highlightBuilder.field("name");
        highlightBuilder.field("time");
        highlightBuilder.preTags("<em>").postTags("</em>");
        highlightBuilder.fragmentSize(200);//高亮内容长度
        searchSourceBuilder.highlight(highlightBuilder);

        // 设置分页
        searchSourceBuilder.from((pageNumber - 1) * pageSize);//设置起始页
        searchSourceBuilder.size(pageSize);//设置页大小
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("farm")// 索引名称
                .build();
        SearchResult result = jestClient.execute(search);

        // 手动解析
//        parseSearchResult(farms, result);

        // 自动解析
        JsonObject jsonObject = result.getJsonObject();
        JsonObject hitsobject = jsonObject.getAsJsonObject("hits");
        long took = jsonObject.get("took").getAsLong();
        long total = hitsobject.get("total").getAsLong();
        List<SearchResult.Hit<Farm, Void>> hits = result.getHits(Farm.class);
        for (SearchResult.Hit<Farm, Void> hit : hits) {
            Farm source = hit.source;
            //获取高亮后的内容
            Map<String, List<String>> highlight = hit.highlight;
            if(highlight != null){
                List<String> provinceList = highlight.get("province");
                if (provinceList != null) {
                    source.setProvince(provinceList.get(0));
                }
                List<String> marketList = highlight.get("market");
                if (marketList != null) {
                    source.setMarket(marketList.get(0));
                }
                List<String> typeList = highlight.get("type");
                if (typeList != null) {
                    source.setType(typeList.get(0));
                }
                List<String> nameList = highlight.get("name");
                if (nameList != null) {
                    source.setName(nameList.get(0));
                }
                List<String> timeList = highlight.get("time");
                if (timeList != null) {
                    source.setName(timeList.get(0));
                }
            }

            Farm farm = new Farm();
            farm.setId(source.getId());
            farm.setProvince(source.getProvince());
            farm.setMarket(source.getMarket());
            farm.setType(source.getType());
            farm.setName(source.getName());
            farm.setStandard(source.getStandard());
            farm.setArea(source.getArea());
            farm.setColor(source.getColor());
            farm.setUnit(source.getUnit());
            farm.setMinPrice(source.getMinPrice());
            farm.setAvgPrice(source.getAvgPrice());
            farm.setMaxPrice(source.getMaxPrice());
            farm.setTime(source.getTime());
            farms.add(farm);
        }
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("farms", farms);
        maps.put("count", total);
        maps.put("took", took);
        return maps;
    }

}
