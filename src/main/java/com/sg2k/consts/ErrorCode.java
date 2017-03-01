package com.sg2k.consts;

public class ErrorCode
{
	public static final int SUCCESS = 200;

	public static final int COMMON_ERROR_CODE = -1;

	public static final int VALITE_CODE_EXPIRE = 1;// 验证码过期

	public static final int VALITE_CODE_ERROR = 2;// 验证码错误

	public static final int UNKNOW_ERROR = 3;// 未知错误

	public static final int PHONE_NOT_FOUND = 4;// 手机号码不存在

	public static final int PARAM_NULL = 5;// 参数为空

	public static final int GET_CODE_FAST = 6;// 验证码获取过快

	public static final int SEND_FAIULRE = 7;// 发送失败

	public static final int DB_ERROR = 8;// 数据库操作异常

	public static final int USER_NO_LOGIN = 9;// 用户未登录

	public static final int OPEN_ID_NOT_BIND = 10;// 微信ID未绑定

	public static final int TEL_BIND_FROM_DIFF = 11;// 同一个账户下已经绑定其他号码

	public static final int TEL_BIND_FROM_SAME = 12;// 同一个手机号码已经被其他微信绑定

	public static final int USER_NOT_EXIST = 13;// 用户不存在

	public static final int USER_COMPANY_TREE_NOT_EXIST = 14;// 用户所在公司导航树不存在

	public static final int USER_COMPANY_NOT_EXIST = 15;// 服务公司不存在

	public static final int CONTRACT_NOT_EXIST = 16;// 合同不存在

}
