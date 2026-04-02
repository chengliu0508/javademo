package com.example.demo.convert;

import com.example.demo.entity.TaskAssigneeEntity;
import com.example.demo.entity.TaskEntity;
import com.example.demo.vo.TaskAssigneeVO;
import com.example.demo.vo.TaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskStructMapper {

    TaskVO toTaskVO(TaskEntity task);

    @Mapping(target = "assignees", ignore = true)
    TaskVO toTaskVOWithoutAssignees(TaskEntity task);

    TaskAssigneeVO toAssigneeVO(TaskAssigneeEntity assignee);
}
