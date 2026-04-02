package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {

    /**
     * 支持按 assigneeId 过滤的分页查询（JOIN task_assignee）
     */
    IPage<TaskEntity> selectPageWithAssignee(
            Page<TaskEntity> page,
            @Param("status") String status,
            @Param("creatorId") Long creatorId,
            @Param("mode") String mode,
            @Param("keyword") String keyword,
            @Param("assigneeId") Long assigneeId
    );
}
