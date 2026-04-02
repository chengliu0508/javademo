package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("task")
public class TaskEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /** 任务创建人 ID */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 任务模式
     * PARALLEL     - 并行：为每个分配人创建独立子任务，各自独立完成
     * COLLABORATIVE - 协作：共享一个任务，所有分配人都完成后任务才结束
     */
    private String mode;

    /**
     * 任务状态: PENDING | IN_PROGRESS | COMPLETED | CANCELLED
     */
    private String status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
