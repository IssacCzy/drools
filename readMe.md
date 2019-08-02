> DrlFileLoadRules2WorkMemoryController
* 直接读取 *.drl 文件加载到 work memory
* 动态拼接 drl string ，并加载到 work memory

> DynamicLoadRules2WorkMemoryController
* 数据库获取 rule 信息，结合模版生成 drl string

> TemplateLoadRules2WrokMemoryController
* 通过自定义模板生成 drl string