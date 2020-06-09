package com.tangchao.shop.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tangchao.shop.beans.TreeEntity;
import lombok.Data;


@Table(name = "goods_type")
@Data
public class GoodsType implements TreeEntity<GoodsType> {

	// 主键,自增
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 标识｛0：正常，-1：删除｝
	private Integer flag;

	// 创建者
	private Long createId;

	// 创建时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	// 修改者
	private Long lastModifyId;

	// 更新时间
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastModifyTime;

	// 父级id
	private Long typePid;

	// 类型名称 中文
	private String typeNameZh;


	// 类型名称 马来文
	private String typeNameMa;


	// 类型名称 英文
	private String typeNameCn;


	// 默认第一级，共3级
	private Integer typeLayer;

	// 排序
	private Integer typeSort;

	// 子菜单
	@Transient
	private List<GoodsType> subTypeList = new ArrayList<>(0);

	@Override
	public Long findId() {
		return this.getId();
	}

	@Override
	public Long findPid() {
		return this.getTypePid();
	}

	@Override
	public Integer findSort() {
		return this.getTypeSort();
	}

	@Override
	public void setSubList(List<GoodsType> typeList) {
		this.subTypeList = typeList;
	}
}
