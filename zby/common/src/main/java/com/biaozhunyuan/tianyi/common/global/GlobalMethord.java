package com.biaozhunyuan.tianyi.common.global;

/**
 * Created by 王安民 on 2017/8/8.
 * 全局的接口类
 */

public class GlobalMethord {
    //信息化采购
    public static final String URL_XXHCG = "/oa/purchase/getList";
    //横向项目申报
    public static final String XMSB = "/oa/horizontalItem/getDeclares";
    //生成评审委托书
    public static final String Y_WTS = "/oa/presidentItem/wtsController/getList";
    //科研委托-合同审批
    public static final String KY_HTSP = "/oa/entrust/contract/getList";
    public static final String HX_HTLB = "/oa/horizontalItem/getContractList";
    public static final String 用户信息 = "oa/humanResources/StaffManagementMobile/getCurrentUser";
    public static final String REDED = "oa/releaseNotice/setReleaseRead";
    public static final String 权限信息 = "oa/humanResources/StaffManagementMobile/getCurrentUserPermission";
    public static final String 功能模块权限 = "oa/humanResources/StaffManagementMobile/getCurrentUserNavigation";
    public static final String 获取Value = "oa/base/phone/getParam";
    public static final String 存储Value = "oa/base/phone/addParam";
    public static final String 添加企业配置 = "oa/base/phone/addBaseConfig";
    public static final String 获取企业配置 = "oa/base/phone/getBaseConfig";
    public static final String 删除企业配置 = "oa/base/phone/deleteBaseConfig";
    public static final String 获取企业资源文件配置 = "crm/js/i18n/json/";
    public static final String 岗位信息 = "oa/humanResources/StaffManagementMobile/getCurrentUserPosition";
    public static final String 未读数量 = "oa/index/personalIndex/bubble";
    public static final String 设置已读 = "oa/oaComment/AddBrowse";
    public static final String 绑定设备 = "oa/app/message/bindToken";
    public static final String 清除设备 = "oa/app/message/clearMobileDeviceToken";
    public static final String 获取字典2 = "oa/dictionaryManage/dictionary/getDict";
    public static final String 上传头像 = "oa/humanResources/StaffManagement/changeAvatar";
    public static final String 工作时间 = "oa/attendance/rule/get";
    public static final String 非外出考勤坐标范围 = "oa/attendance/map/rule/get";
    public static final String 获取H5灵活菜单 = "oa/h5Menu/getH5EntryList";


    public static final String 登录 = "oa/auth/permission/login";
    public static final String 获取域名配置 = "oa/auth/permission/detectStandaloneServer";
    public static final String 查询标签 = "crm/label/getLabelList";
    public static final String 保存标签 = "oa/customer/saveCustomerLabel";
    public static final String 注册账号 = "oa/registerDetail/save";
    public static final String 注册检查手机号 = "oa/registerDetail/checkPhoneNumber";
    public static final String 注册检查验证码是否正确 = "oa/registerDetail/checkVerifyCode";
    public static final String 注册保存注册信息 = "oa/registerDetail/save";
    public static final String 服务条款 = "register/agreement";
    public static final String 得到手机验证码 = "oa/backPassword/backPassword1";
    public static final String 发送验证码 = "oa/registerDetail/getVerifyCode";
    public static final String 企业是否存在 = "oa/backPassword/checkInfo";
    public static final String 校验验证码是否正确 = "oa/backPassword/checkVerifyCode";
    public static final String 更改密码 = "oa/backPassword/findPassword";
    public static final String 重置密码 = "oa/humanResources/StaffManagement/DoResetPwd";


    public static final String 下属员工 = "oa/officeRoutine/department/boosBelowStaff";
    //    public static final String 全体员工 = "oa/humanResources/StaffManagement/getAllStaff";
    public static final String 全体员工 = "oa/humanResources/StaffManagement/getStaffPart";
    public static final String 部门员工 = "oa/humanResources/StaffManagement/getStaffList";
    public static final String 公司员工 = "oa/humanResources/StaffManagement/getFilialeStaff?deptId=";
    public static final String 员工搜索 = "oa/humanResources/StaffManagement/getAPPStaffAddressBook?pageIndex=0&pageSize=99&str=";
    public static final String 员工姓名 = "form/workflowConfig/GetStaffNames";
    public static final String 部门树 = "wf/outside/getTree";
    public static final String 获取部门员工 = "wf/outside/getOutsideBaseStaff";

    public static final String 部门列表 = "oa/base/department/getAppDeptTree";
    public static final String 部门 = "oa/workflow/workflowList/listForMobile";

    public static final String 考勤 = "oa/attendance/Attendance/add";
    public static final String 考勤记录 = "oa/attendance/Attendance/get";
    public static final String 外出定位记录 = "oa/attendance/Attendance/getOutLocation";
    public static final String 原因说明 = "oa/attendance/Attendance/lateOrEarlyReason";


    public static final String 附件列表 = "oa/common/attachment/index";
    public static final String 显示附件 = "oa/common/attachment/downloadFile?id=";
    public static final String 下载附件 = "oa/common/attachment/downloadTempFile?fileName=";
    public static final String 显示H5菜单 = "oa/common/attachment/downH5Logo?id=";
    public static final String 上传附件 = "oa/common/attachment/uploadFile";
    public static final String 保存客户往来文件 = "crm/correspondence/save";
    public static final String 上传发文附件 = "oa/attr/attr/add";
    public static final String 下载往来文件 = "oa/common/attachment/downloadFiles?ids=";

    public static final String 日志列表 = "oa/workRecordManage/workRecord/list";
    //    public static final String 日志过滤 = "oa/workRecordManage/workRecord/selectByDateOrNameAndContent";
    public static final String 日志过滤 = "oa/workRecordManage/workRecord/searchList";
    public static final String 日志保存 = "oa/workRecordManage/workRecord/save";
    public static final String 当天日志 = "oa/workRecordManage/workRecord/addOrEdit";
    public static final String 获取日志 = "oa/workRecordManage/workRecord/get";
    public static final String 日志添加评论 = "oa/oaComment/AddComment";
    public static final String 日志评论列表 = "oa/oaComment/GetComment";
    public static final String 日志点赞和取消点赞 = "oa/oaComment/AddUpvote";
    public static final String 日志点赞列表 = "oa/oaComment/GetUpvote";


    public static final String 通知列表 = "oa/noticeManage/notice/commonList";
    public static final String 通知过滤 = "oa/noticeManage/notice/getNoticeList";
    public static final String 新建通知 = "oa/noticeManage/notice/add";


    public static final String 任务列表 = "oa/workScheduleManage/workSchedule/list";
    public static final String 任务过滤 = "oa/workScheduleManage/workSchedule/selectByDateOrExecutorAndContent";
    public static final String 任务保存 = "oa/workScheduleManage/workSchedule/save";
    public static final String 周期任务保存 = "oa/workScheduleManage/workSchedule/saveOaCycleTask";
    public static final String 周期任务列表 = "oa/workScheduleManage/workSchedule/lookOaCycleTask";
    public static final String 改变任务状态 = "oa/workScheduleManage/workSchedule/changeStatus";
    public static final String 任务查看员工 = "oa/workScheduleManage/workSchedule/getCollegueTaskSummary";
    public static final String 获取项目名称 = "oa/workScheduleManage/workSchedule/getProjectNames";
    public static final String 任务过滤项目 = "oa/bugManage/bugRecord/selectCompany";
    public static final String 任务看板列表 = "oa/workScheduleManage/WorkTaskPanel/lookWorkTaskPanel";
    public static final String 新建任务看板 = "oa/workScheduleManage/WorkTaskPanel/saveWorkTaskPanel";
    public static final String 任务看板详情 = "oa/workScheduleManage/WorkTaskPanel/getWorkScheduleInfoByOaWorkTaskPanel?uuid=";
    public static final String 任务看板详情操作列 = "oa/workScheduleManage/WorkTaskPanel/savePanleInfo";
    public static final String 任务看板详情移动列 = "oa/workScheduleManage/WorkTaskPanel/exchangeLaneSort";
    public static final String 任务看板详情操作卡片 = "oa/workScheduleManage/WorkTaskPanel/updateWorkTaskInfo";
    public static final String 任务看板详情添加卡片 = "oa/workScheduleManage/WorkTaskPanel/savePanleWorkTaskInfo";
    public static final String 任务看板详情删除一列 = "oa/workScheduleManage/WorkTaskPanel/deletePlaneInof?uuid=";
    public static final String 任务看板详情添加成员 = "oa/workScheduleManage/WorkTaskPanel/saveWorkTaskLaneStaffInfo";
    public static final String 任务看板详情移除成员 = "oa/workScheduleManage/WorkTaskPanel/deleteWorkTaskLaneStaffInfo";
    public static final String 任务看板详情保存卡片 = "oa/workScheduleManage/workSchedule/editWorkTaskInfo";
    public static final String 任务看板详情删除卡片 = "oa/workScheduleManage/workSchedule/delete";
    public static final String 任务未完成状态日期 = "oa/workScheduleManage/workSchedule/selectWorkScheduleByDate";


    public static final String 考勤列表 = "oa/attendance/attendance/getList";
    public static final String 考勤过滤 = "oa/attendance/attendance/getAttendanceByTimeAndStaff";
    public static final String 全部考勤 = "oa/attendance/attendance/getStaffAttendanceAll";
    public static final String 考勤统计 = "oa/attendance/attendance/getThisMonthAttendance";


    public static final String 新建申请列表 = "oa/workflow/workflowList/listForMobile";
    //    public static final String CRM新建申请列表 = "wf/workflow/workflowList/listForMobile";
    public static final String 待我审批 = "wf/workflow/workflowList/getReceiveList";
    public static final String 我审批的 = "wf/workflow/workflowList/getAuditedList";
    public static final String 我发起的 = "wf/workflow/workflowList/getMyList";
    public static final String 抄送列表 = "wf/workflow/workflowList/copyToMyList";
    public static final String 全部申请 = "oa/workflow/workflowList/getAllList";
    public static final String 表单详情 = "wf/form/vsheet/formForMobile";
    public static final String 审批意见 = "wf/form/vsheet/getLcgcList";
    public static final String 审批前检查 = "wf/form/vsheet/beforeAuditCheck";
    public static final String web表单详情 = "wf/form/vsheet/form";
    public static final String CRM审批意见 = "wf/form/vsheet/getLcgcList";
    public static final String 保存表单 = "wf/form/vsheet/saveData";
    public static final String 提交表单 = "wf/form/vsheet/submit";
    public static final String 删除申请 = "oa/workflow/workflowList/deleteWorkFlowByIds";
    public static final String 审批申请 = "wf/form/vsheet/Audit";
    public static final String 表单转为PDF = "wf/form/vsheet/toPdf";
    public static final String 抄送 = "wf/formLinkage/formCopyTo/copyTo";
    public static final String 流程节点 = "wf/form/vsheet/formForMobileGetNodes";
    public static final String 自由审批添加节点 = "wf/form/vsheet/addFreeApprovalNode";
    public static final String 自由审批删除节点 = "wf/form/vsheet/deleteFreeApprovalNode";
    public static final String 退回申请 = "oa/form/vsheet/backWorkflow";
    public static final String 申请名称列表 = "oa/common/CommonApi/GetZLSelectDict?dictionaryName=workflow_template&colName=formName&key=";
    public static final String 调休和请假的时间计算 = "oa/formLinkage/FormLinkage/sumDaysByLeave";
    public static final String 出差的时间计算 = "oa/formLinkage/FormLinkage/sumDaysByOnbusiness";
    public static final String 表单联动 = "wf/form/vsheet/loadRelated";
    public static final String 转下一步审核人 = "oa/formHandler/form/forward";
    public static final String 撤回子流程 = "oa/workflow/VSheet/forwardRecallFormBtn";
    public static final String 获取表单套打模板 = "wf/document/checkModelFileExist";
    public static final String 表单套打模板下载 = "wf/document/printDocx";
    public static final String 改变抄送申请状态 = "oa/workflow/workflowList/changeCopyStatus";
    public static final String 设置申请已读 = "wf/workflow/workflowList/insertReadRecord";
    public static final String 获取表单引擎JS代码 = "wf/form/vsheet/script?workflowTemplateId=";
    public static final String 表单审批意见H5 = "wf/html/formDisplay/shenPiYiJian";
    public static final String 表单检视流转H5 = "wf/html/formDisplay/jianShiLiuZhuan";
    public static final String 表单审批选择出口 = "wf/workflow/VSheet/getWorkflowNodeSelect";
    public static final String 获取岗位员工 = "oa/common/StaffSelect/GetStaffByPost";
    public static final String 获取岗位员工2 = "wf/workflow/gongWen/selectStaffByType";
    public static final String 获取审批意见 = "wf/workflow/gongWen/getCommonOptionByCreatorId";
    public static final String 获取工作日 = "oa/kaoQin/getWorkDayS";
    public static final String 获取workFlowId = "wf/workflow/gongWen/getWorkInsByIdAndTempId";


    public static final String 获取企业CRM配置 = "crm/crmConfig/getCrmConfigByName";
    public static final String 客户列表 = "crm/customer/getCustomerList";//?id=";
    public static final String 公海客户列表 = "crm/customer/gongHai/getCustomerList";//?id=";
    public static final String 客户详情项目列表 = "crm/crm/project/getProjectBycustomerId?customerId=";
    public static final String 客户详情其他联系方式 = "crm/customer/getOtherContactList?customerId=";
    public static final String 客户详情商机列表 = "crm/salesChance/listChanceByCustomer?customerId=";
    public static final String 客户详情修改记录列表 = "crm/customer/getEditHistoryList?customerId=";
    public static final String 客户详情分配记录列表 = "crm/customer/listAssignRecord?customerId=";
    public static final String 客户详情动态台账列表 = "crm/ledger/getLedgerList?customerId=";
    public static final String 客户详情台账数据列表 = "crm/ledger/getLedgerDataList?tableName=";
    public static final String 客户详情台账条目显示字段 = "oa/common/html/Generate";
    public static final String 客户详情台账删除 = "crm/ledger/deleteLedgerData?tableName=";
    public static final String 客户详情表单 = "crm/dynamicTab/getFormDataList?customerId=";
    public static final String 客户详情动态H5 = "#/tab";
    public static final String 客户详情电子合同H5 = "#/EleContract";
    public static final String 客户已共享列表 = "crm/customer/getShareStaff?customerId=";
    public static final String 客户详细信息 = "crm/customer/getCustomerById?id=";
    public static final String 共享客户 = "crm/share/addSharePermission";
    public static final String 共享客户新建权限 = "crm/share/getPermissionByStaffId";
    public static final String 动态字段 = "crm/common/tableGrid/tableGrid";
    public static final String CRM动态字段 = "crm/common/tableGrid/tableGrid";
    public static final String 保存动态字段 = "crm/common/tableGrid/saveDataAndReturnId";
    public static final String 客户分类 = "crm/homepage/getPlanContactCustomerGridData";
    public static final String 客户详情 = "crm/homepage/getOrderPerformanceInfo?type=1&timeFilter=month";
    public static final String 判断是否可以新建客户 = "crm/customer/creatable";
    public static final String 获取客户联系方式 = "crm/customer/getOtherContactList";
    public static final String 判断公海客户是否可被分配 = "crm/customer/distributable";
    public static final String 分配客户 = "crm/customer/assign";
    public static final String 提取公海客户 = "crm/customer/gongHai/extractGonghai";
    public static final String 将客户放入公海 = "crm/customer/gongHai/inGonghai";
    //    public static final String 获取字典 = "oa/common/CommonApi/getDictionary";
    public static final String 获取字典 = "oa/common/CommonApi/GetZLSelectDict";
    public static final String 校验动态字段重复 = "crm/common/tableGrid/validateMultiUnique";
    public static final String 防止项目掉入公海 = "crm/crm/project/getRemindProjectList";
    public static final String 设置表单不再提醒 = "crm/payment/cancelPush?workflowId=";

    public static final String 产品列表 = "oa/product/list?reserveProduct=true&status=2";
    public static final String 产品详情2 = "html/finance/productManage/H5/detail?id=";


    public static final String 合同列表 = "crm/contract/getContractList";
    public static final String 获取单据详情标签页 = "crm/dynamicTab/getIndependentDynamicForm";
    public static final String 单据详情表单 = "crm/dynamicTab/getFormDataList?formTableName=";


    public static final String 预约列表 = "oa/reserve/list";
    public static final String 预约查看期限 = "oa/product/getDurations";
    public static final String 预约查看份额类型 = "oa/product/getCommissionRate";
    public static final String 预约验证客户 = "oa/customer/validateCustomerForContract";
    public static final String 产品期次 = "oa/product/issue/getProductCurrentIssue";
    public static final String 验证额度 = "oa/reserve/getReserveInfoByProductId";


    public static final String 市场活动 = "crm/activity/list";
    public static final String 活动详情 = "crm/html/market/activityDetail?activityId=";
    public static final String 活动地址 = "oa/market/customerdevelopment/creatMarketingActivityUrl";
    public static final String 客户活动列表 = "oa/market/customerdevelopment/save";
    public static final String 选择可预约活动的客户列表 = "crm/customer/getCustomerByActivity?activityId=";
    public static final String 预约活动 = "crm/activity/addAppointment";


    public static final String 跟进记录列表 = "crm/crm/contact/list";
    public static final String 跟进记录列表2 = "crm/crm/contact/getContact";
    public static final String 跟进记录状态 = "crm/crm/contact/getStage";
    public static final String 添加跟进记录 = "crm/crm/contact/addContact";
    public static final String 跟进记录分类 = "crm/customer/getStageList";


    public static final String 动态列表 = "oa/app/message/getMessageListByType?plannerId=";//type=fb5f5d140ce94883b744f16081a8f58c&
    public static final String 动态详情 = "oa/app/message/getList";


    public static final String 点赞列表 = "oa/like/like/query";
    public static final String 点赞 = "oa/like/like/add";
    public static final String 取消点赞 = "oa/like/like/delete";
    public static final String 评论列表 = "oa/comment/comment/query";
    public static final String 评论 = "oa/comment/comment/add";

    public static final String 空间列表 = "oa/zone/post/getNewestPost";
    public static final String 空间详情H5 = "html/oa/zoneH5/detail?isHiddenTitle=1&uuid=";
    public static final String 空间列表过滤 = "oa/zone/post/getPostListByType";
    public static final String 个人空间 = "oa/zone/post/getPersonalListTable";
    public static final String 部门空间 = "oa/zone/post/getDepartmentTable";
    public static final String 公司空间 = "oa/zone/post/getCompanyTable";
    public static final String 发布空间 = "oa/zone/post/savePost";

    public static final String 资讯详情 = "oa/hotNewsManage/hotNews/get?uuid=";
    public static final String 审批语句 = "oa/common/CommonApi/GetZLSelectDict?dictionaryName=workflow_instance_common_option";

    public static final String 待办收文列表 = "oa/governmentDocument/receiveManage/getReceiveList?nodeId=-1";
    public static final String 待阅收文列表 = "oa/governmentDocument/receiveManage/getDueInList";
    public static final String 已办收文列表 = "oa/governmentDocument/receiveManage/getReceivedList?filed=-1";
    public static final String 全部收文列表 = "oa/governmentDocument/receiveManage/getAllList?time=%E5%85%A8%E9%83%A8";


    public static final String 待办发文列表 = "oa/governmentDocument/sendManage/getReceiveList?nodeId=-1";
    public static final String 已办发文列表 = "oa/governmentDocument/sendManage/getFinishedList?filed=-1";
    public static final String 我的发文列表 = "oa/governmentDocument/sendManage/getMyList?filed=-1";
    public static final String 全部发文列表 = "oa/governmentDocument/sendManage/getAllList?time=全部";

    public static final String 待办事项 = "oa/mobile/indexNotice/getToDoList";
    public static final String 未读通知 = "oa/mobile/indexNotice/getNoReadNotice";
    public static final String 项目列表 = "crm/crm/project/getProjectList";
    public static final String 项目新建表单获取参数 = "crm/dynamicTab/getFormParameter";
    public static final String 项目判断当前是否可以新建表单 = "crm/dynamicTab/getFormDataNumber";
    public static final String 项目联系记录 = "crm/crm/contact/getContact";
    public static final String 联系记录根据ID查详情 = "crm/customer/getRecordById?uuid=";
    public static final String 项目里程碑 = "crm/crm/project/getMileStoneList?id=";
    public static final String 项目设备列表 = "crm/crm/projectEquipment/getProjectEquipment?projectId=";
    public static final String 项目设备列表详情 = "#/equipment?equipmentId=";
    public static final String 项目表单 = "crm/dynamicTab/getFormDataList?projectId=";
    public static final String 项目获取当前打卡信息 = "crm/project/attendance/current";
    public static final String 项目签到 = "crm/project/attendance/signIn";
    public static final String 项目签退 = "crm/project/attendance/signOut";
    public static final String 项目打卡记录 = "crm/project/attendance/list/currentUser";
    public static final String 获取关联表单 = "crm/dynamicTab/getRelevanceDynamicForm?dynamicTabId=";
    public static final String 商机列表 = "crm/salesChance/getData";
    public static final String 获取商机编号 = "crm/salesChance/getChanceByCode?code=";
    public static final String 里程碑保存 = "crm/milestone/saveMileStoneByuuid";
    public static final String 客户往来文件 = "crm/correspondence/getList?customerId=";
    public static final String 验证里程碑月份是否为空 = "crm/crm/project/mileStoneIsPlanned?projectId=";
    public static final String 验证里程碑阶段是否提交过 = "crm/crm/project/getMileStoneVsheetId";
    public static final String 商机地址校验 = "crm/map/compareMileBetweenAddress";
    public static final String 验证商机是否报备 = "crm/salesChance/getTransformProject";
    public static final String 保存商机经纬度 = "crm/salesChance/updateChance";
    public static final String 项目详情标签页 = "crm/dynamicTab/getSelectedList?host=project";
    public static final String 客户详情标签页 = "crm/dynamicTab/getSelectedList?host=customer";
    public static final String 考试中心列表 = "trng/questionnair/getQuestionnaireAnswered";
    public static final String 课程中心列表 = "trng/training/document/getDocumentList";
    public static final String 查看答卷 = "trng/html/search/questionnaireAnsweredLook?id=";
    public static final String 重做答卷 = "trng/html/search/questionnairePreview?isForSaved=true&id=";
    public static final String 课程详情 = "trng/html/app/training/documentManagementDetail?id=";
    public static final String 莱恩斯统计图 = "crm/html/expenses/lionsStatistics";
    public static final String 莱恩斯合同表单数据 = "crm/contract/getContractFormData?contractId=";

    public static final String 未读消息 = "oa/mobile/im/im/getUnReadMessage";
    public static final String 新增会话 = "oa/mobile/im/im/addGroup";
    public static final String 获取群组信息 = "oa/mobile/im/im/getImGroupById";
    public static final String 设置消息已读 = "oa/mobile/im/im/updateMessageStatus";
    public static final String 获取群组成员 = "oa/mobile/im/im/getGroupStaff";
    public static final String 修改群组名称 = "oa/mobile/im/im/changeGroupName";
    public static final String 新增群组成员 = "oa/mobile/im/im/addGroupStaff";
    public static final String 删除群组成员 = "oa/mobile/im/im/deleteGroupStaff";
    public static final String 获取会话列表 = "oa/mobile/im/im/getSessionList";
    public static final String 获取群组会话列表 = "oa/mobile/im/im/getDepartGroupList";
    public static final String 获取消息记录 = "oa/mobile/im/im/getHistoryMessage";
    public static final String 获取群组的图片记录 = "oa/mobile/im/im/getGroupImage";
    public static final String 获取群组的文件记录 = "oa/mobile/im/im/getGroupFile";
    public static final String 搜索群聊天记录 = "oa/mobile/im/im/searchGroup";
    public static final String 搜索全部群组 = "oa/mobile/im/im/searchGroupNameAndStaffNameAll";
    public static final String 搜索全部群聊记录 = "oa/mobile/im/im/searchMessageAll";
    public static final String 搜索全部聊天记录 = "oa/mobile/im/im/searchAll";
    public static final String 查看消息前后聊天记录 = "oa/mobile/im/im/searchMessageByMessageId";
    public static final String 撤回消息 = "oa/mobile/im/im/recall";
    public static final String 删除消息 = "oa/mobile/im/im/deleteMessage";
    public static final String 置顶消息 = "oa/mobile/im/im/setImTopSort";
    public static final String 消息免打扰 = "oa/mobile/im/im/closeNoticeReceive";
    public static final String 取消消息免打扰 = "oa/mobile/im/im/openNoticeReceive";
    public static final String 清空聊天记录 = "oa/mobile/im/im/clearImMessage";
    public static final String 删除并退出群组 = "oa/mobile/im/im/quitImStaff";
    public static final String 取消置顶消息 = "oa/mobile/im/im/cancelImTopSort";
    public static final String 获取群组是否只群主可以添加成员 = "oa/mobile/im/im/getGroupManagerAddAndDeleteStaffOnly";
    public static final String 设置群组是否只群主可以添加成员 = "oa/mobile/im/im/updateGroupManagerAddAndDeleteStaffOnly";


    public static final String 工单列表 = "crm/workorder/list";
    public static final String 工单获取流程编号 = "crm/dynamicTab/getSelectedList?host=customer";
    public static final String 模糊搜索所用参数 = "oa/common/html/Generate";
    public static final String 模糊搜索 = "crm/systemConfig/fieldDescription/getFuzzySearchField";
    public static final String 查询是否可直接新建 = "crm/crmConfig/getCrmConfig";


    public static final String 线索列表 = "crm/clue/getClueList";
    public static final String 线索详情 = "crm/clue/getClueById?uuid=";
    public static final String 线索分配 = "crm/clue/distribution";
    public static final String 线索转化 = "crm/clue/translateClueToSaleChance";
    public static final String 线索关闭 = "crm/clue/close";
    public static final String 线索删除 = "crm/clue/deleteClue";
    public static final String 线索保存 = "crm/clue/clueEdit";


    public static final String 新增BUG = "oa/bugManage/bugRecord/saveBug";
    public static final String 编辑BUG = "oa/bugManage/bugRecord/editBugDetails";
    public static final String 获取BUG = "oa/bugManage/bugRecord/getBugDetails";
    public static final String BUG列表 = "oa/bugManage/bugRecord/getBugList?type=designates&projectManagement=";
    public static final String 获取可选择的项目 = "oa/bugManage/bugRecord/selectCompany";


    public static final String 进销存商品详情查询 = "psi/barcode/getSkuBarcodeList";
    public static final String 进销存商品详情和库存查询 = "psi/vsheetEvent/getSkuInfoAndBalance";


    public static final String 资讯列表 = "crm/mall/information/list?category=1";
    public static final String 资讯详情H5 = "crm/html/mall/h5/information";


    public static final String 微信联系人列表 = "crm/wechat/contact/getWechatContactListByStaffId";
    public static final String 微信群聊列表 = "crm/wechat/Chatroom/getWechatChatroomList";
    public static final String 微信聊天消息 = "crm/wechat/message/getWechatMsgByWechatId";
    public static final String 最近微信聊天员工 = "crm/wechat/contact/getRecentChatStaffId";





    public static final String 标准院获取手机验证 = "oa/auth/permission/getLoginYZM";

    public static final String 标准院验证手机验证码 = "oa/auth/permission/checkLoginYZM";

    public static final String 院内共享列表 = "oa/releaseNew/getSharNewList";//oa/releaseNew/getReleaseNewSyShare   //oa/releaseNew/getReleaseNewList
    public static final String 标准院首页栏目 = "oa/releaseNew/getReleaseNewList";
    public static final String 标准院通知 = "oa/releaseNotice/getReleaseNoticeList";
    public static final String 标准院首页栏目详情 = "html/personaloffice/showRelease";//html/personaloffice/showRelease
    public static final String 院内共享详情 = "html/personaloffice/showShar";



    public static final String 标准院我的待办 = "oa/personalOffice/getBacklogIngList";

    public static final String 标准院原系统待办 = "oa/international/getDaiBanData";


    public static final String 标准院待办文件 = "oa/personalOffice/getBacklogIngList";


    public static final String 标准院公文列表 = "oa/international/getFormList";
    public static final String 疫情报备列表 = "oa/releaseNew/getEpidemicList";
    public static final String 疫情报备已办列表 = "oa/releaseNew/getEpidemicReportYbList?category=sdfsdf98894d8f4s5d4f1e98w1r84d1";

    public static final String 标准院待办列表 = "oa/workflow/workflowList/getDbListByCategory";
    public static final String 标准院已办列表 = "oa/workflow/workflowList/getYbListByCategory";

    public static final String 标准院我的签报列表 = "oa/workflow/workflowList/getFormListByCategory";

    public static final String 标准院表单审批意见 = "wf/workflow/gongWen/getOpinionByWorkflowId";





}
