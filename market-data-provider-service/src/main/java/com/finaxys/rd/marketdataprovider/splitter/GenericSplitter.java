package com.finaxys.rd.marketdataprovider.splitter;

import java.util.List;

import com.finaxys.rd.dataextraction.domain.msg.Splittable;

public class GenericSplitter<T extends Splittable> {



	public  List<?> split(T t){
		
			return t.split();
	}
}
