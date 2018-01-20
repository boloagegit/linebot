package io.abner.linebot.service;

import io.abner.linebot.Repository.CommandRepository;
import io.abner.linebot.dto.CommandDTO;
import io.abner.linebot.utility.IdUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActionService {

    @Autowired
    private CommandRepository commandRepository;

    public void saveOrUpdateCommand(CommandDTO dto) {

        CommandDTO saveDto = new CommandDTO();

        if (StringUtils.isNotBlank(dto.getName())) {
            CommandDTO existDto = commandRepository.findTopByName(dto.getName());

            if (existDto != null) {
                existDto.setName(dto.getName());
                existDto.setR(dto.getR());
                commandRepository.save(existDto);
            } else {
                BeanUtils.copyProperties(dto, saveDto);
                saveDto.setId(IdUtils.generateId());
                commandRepository.insert(saveDto);
            }
        }
    }

    public void updateCommand(String name, String r) {
        CommandDTO dto = commandRepository.findTopByName(name);
        if (dto != null) {
            dto.setR(r);

            commandRepository.save(dto);
        }
    }

    public List<CommandDTO> findAllCommand() {
        return commandRepository.findAll();
    }

    public void deleteCommand(String name) {
        if (StringUtils.equals(name, "#All")) {
            commandRepository.deleteByNameNot("指令");
        } else {
            commandRepository.deleteByName(name);
        }
    }

}
