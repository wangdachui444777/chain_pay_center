## 通用规则
- 永久以中文进行思考与回复，确保所有对外沟通均为中文。
- 在修改项目代码后，务必执行语法/编译检查，至少选择其中一项，确保无误；
- 数据库表结构在目录sql/chain_pay_center.sql，其中表前缀bc_开头的为业务表，sys_表前缀为系统框架表。
- 其中业务相关的代码路径：1基本的实体类和mapper相关在ruoyi-system/src/main/java/com/ruoyi/blockchain/*，
  2：定时任务入口：ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/*，定时相关代码：ruoyi-quartz/src/main/java/com/ruoyi/bc/*
  3：外部公共接口：ruoyi-admin/src/main/java/com/ruoyi/api/controller/bc/*
- 后台相关的业务代码路径：1：前端代码ruoyi-admin/src/main/resources/templates，后端使用接口：ruoyi-admin/src/main/java/com/ruoyi/web/controller/blockchain
- 在进行任何任务之前，必须先完整阅读`dev_log`目录下当天的日志，并根据当前任务中最重要的一些关键词用英文搜索 `dev_log` 目录下的相关信息，确保了解上下文。
- 在完成任何任务并汇报总结之前，必须遵循 `dev_log/readme.md` 的规则添加一条日志。所添加的规则中，请将详细的目录，文件名，等重要文件进行标记。包含项目内的目录，或者需要参考的项目外的目录。