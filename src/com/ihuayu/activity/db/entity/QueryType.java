/**
 * 
 */
package com.ihuayu.activity.db.entity;

/**
 * @author lixingwang
 *
 */
public enum QueryType {
	
   EN {
        @Override
        public String getName() {
            return "en2sc";
        }
    },
    CN {
        @Override
        public String getName() {
            return "sc2en";
        }
    };
	public abstract String getName();
}
