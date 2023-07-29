package com.hk.entity.query;



/**
 * @Description:系统设置信息查询对象
 * @author:AUTHOR
 * @date:2023/07/29
 */
public class SysSettingQuery extends BaseQuery {

	/**
	 * 编号
	 */
	private String code;

	private String codeFuzzy;

	/**
	 * 设置信息
	 */
	private String jsonContent;

	private String jsonContentFuzzy;

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}

	public String getJsonContent() {
		return this.jsonContent;
	}

	public void setCodeFuzzy(String codeFuzzy) {
		this.codeFuzzy = codeFuzzy;
	}

	public String getCodeFuzzy() {
		return this.codeFuzzy;
	}

	public void setJsonContentFuzzy(String jsonContentFuzzy) {
		this.jsonContentFuzzy = jsonContentFuzzy;
	}

	public String getJsonContentFuzzy() {
		return this.jsonContentFuzzy;
	}

}