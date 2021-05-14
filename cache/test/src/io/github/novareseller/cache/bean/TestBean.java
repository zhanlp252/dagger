package io.github.novareseller.cache.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TestBean implements Serializable{

	private static final long serialVersionUID = -1873808348888831398L;

	private String name;

	private Integer age;

}
