package no.fint.audit.plugin.mongo.admin.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by oystein.amundsen on 08.02.2017.
 */
public class PageableAuditEventGroup implements Serializable {
    private Long                       totalItems = 0L;
    private Long                       page = 0L;
    private Long                       pageSize = 0L;
    private List<MongoAuditEventGroup> data;

    public List<MongoAuditEventGroup> getData() {
        return data;
    }

    public PageableAuditEventGroup setData(List<MongoAuditEventGroup> data) {
        this.data = data;
        return this;
    }

    public Long getPage() {
        return page;
    }

    public PageableAuditEventGroup setPage(Long page) {
        this.page = page;
        return this;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public PageableAuditEventGroup setPageSize(Long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Long getPageCount() {
        return totalItems / pageSize;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public PageableAuditEventGroup setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
        return this;
    }
}
