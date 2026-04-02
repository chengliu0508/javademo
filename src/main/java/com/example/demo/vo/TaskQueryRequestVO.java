package com.example.demo.vo;

import lombok.Data;

@Data
public class TaskQueryRequestVO {

    /** 任务状态过滤: PENDING | IN_PROGRESS | COMPLETED | CANCELLED */
    private String status;

    /** 按分配人 ID 过滤 */
    private Long assigneeId;

    /** 按创建人 ID 过滤 */
    private Long creatorId;

    /** 任务模式过滤: PARALLEL | COLLABORATIVE */
    private String mode;

    /** 关键词搜索（匹配 title/content） */
    private String keyword;

    private int page = 1;

    private int size = 10;
}
