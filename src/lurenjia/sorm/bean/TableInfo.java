package lurenjia.sorm.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装了一个表的所有字段信息，包括表名String、所有字段信息Map<String,ColumnInfo>、唯一主键。
 * @author lurenjia
 * @date 2022/12/2-15:46
 */
public class TableInfo {
    /**
     * 表名
     */
    private String name;

    /**
     * 所有字段信息
     */
    private Map<String,ColumnInfo> columns;

    /**
     * 唯一主键（暂且只处理表中有且只有一个主键的情况）
     */
    private ColumnInfo onlyPriKey;

    /**
     * 如果有联合主键，在这里储存。
     */
    private List<ColumnInfo> priKey;

    public TableInfo() {
    }

    public TableInfo(String name, Map<String, ColumnInfo> columns, ColumnInfo onlyPriKey) {
        this.name = name;
        this.columns = columns;
        this.onlyPriKey = onlyPriKey;
    }

    public TableInfo(String tableName, ArrayList<ColumnInfo> columnInfos, HashMap<String, ColumnInfo> stringColumnInfoHashMap) {
        this.name = tableName;
        this.priKey = columnInfos;
        this.columns = stringColumnInfoHashMap;
    }

    public List<ColumnInfo> getPriKey() {
        return priKey;
    }

    public void setPriKey(List<ColumnInfo> priKey) {
        this.priKey = priKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ColumnInfo> columns) {
        this.columns = columns;
    }

    public ColumnInfo getOnlyPriKey() {
        return onlyPriKey;
    }

    public void setOnlyPriKey(ColumnInfo onlyPriKey) {
        this.onlyPriKey = onlyPriKey;
    }
}
