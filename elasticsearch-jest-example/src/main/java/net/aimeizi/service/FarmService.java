package net.aimeizi.service;

import java.util.Map;

/**
 * Created by Administrator on 2015/9/21.
 */
public interface FarmService {

    /**
     * 根据field和queryString全文检索
     * @param field
     * @param queryString
     * @param older
     * @param pageNumber
     * @param pageSize
     * @return
     */
    Map<String,Object> search(String field, String queryString, String older, int pageNumber, int pageSize)  throws Exception;


}
