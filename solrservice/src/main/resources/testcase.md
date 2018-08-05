# 二期接口测试用例

## 图像添加接口

- http://padl.paic.com.cn/cvg/app/imgRepeatAdd

1. case1:　正常添加
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
            "originName": "originName1",
            "uploadPath": "改"
        }
    ]
}
```

2. case2: 添加两张同样的图片，并且id、businessKey等字段都相同
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
            "originName": "originName1",
            "uploadPath": "改"
        },
		{
            "documentId": "documentId1",
            "businessKey": "businessKey1",
            "originName": "originName1",
            "uploadPath": "改"
        }
    ]
}
```

3. case3: 一次添加多张图片
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
            "originName": "originName1",
            "uploadPath": "改"
        },
		{
            "documentId": "documentId2",
            "businessKey": "businessKey2",
            "originName": "originName2",
            "uploadPath": "改"
        },
		{
            "documentId": "documentId3",
            "businessKey": "businessKey3",
            "originName": "originName3",
            "uploadPath": "改"
        }
    ]
}
```

4. case4: 传递的参数不正确
```
{

}
```

5. case5: 从uploadPath下载图片失败，这会导致本次请求中所有图片添加失败
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
            "originName": "originName1",
            "uploadPath": "正确"
        },
		{
            "documentId": "documentId2",
            "businessKey": "businessKey2",
            "originName": "originName2",
            "uploadPath": "正确"
        },
		{
            "documentId": "documentId3",
            "businessKey": "businessKey3",
            "originName": "originName3",
            "uploadPath": "错误"
        }
    ]
}
```

6. case6: 添加两张同样的图片，并且id、businessKey等字段不相同
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
            "originName": "originName1",
            "uploadPath": "改"
        },
		{
            "documentId": "documentId2",
            "businessKey": "businessKey2",
            "originName": "originName2",
            "uploadPath": "改，与上面相同"
        }
    ]
}
```

## 图像删除接口

- http://padl.paic.com.cn/cvg/app/imgRepeatDelete

1. case1: 正常删除,注意只要参数传递正确，不管要删除的图片是否存在，都会返回删除成功信息
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
            "originName": "originName1",
        }
    ]
}
```

2. case2: 参数不正确
```
{

}
```

3. case3: documentInfo中字段缺失
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
        }
    ]
}
```

## 图像查重接口

- http://padl.paic.com.cn/cvg/app/imgRepeat

1. case1: 正常情况，两张图片都重复
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
			“uploadPath”: "重复"
        },
		{
            "documentId": "documentId2",
            "businessKey": "businessKey2",
			“uploadPath”: "重复"
        }
    ]
}
```

2. case2: 正常情况，两张图片中有一张图片重复
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
			“uploadPath”: "rjfdsajf4329fdsaljdsi"
        },
		{
            "documentId": "documentId2",
            "businessKey": "businessKey2",
			“uploadPath”: "不重复"
        }
    ]
}
```

3. case3: 参数传递不正确
```
{

}
```

4. case4: 从uploadPath下载图片失败
```
{
    "reportNo": "320487903275",
    "documentInfo": [
        {
            "documentId": "documentId1",
            "businessKey": "businessKey1",
			“uploadPath”: "错误的地址"
        }
    ]
}
```
