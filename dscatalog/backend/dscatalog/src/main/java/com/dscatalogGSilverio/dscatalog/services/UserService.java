package com.dscatalogGSilverio.dscatalog.services;

import com.dscatalogGSilverio.dscatalog.dto.CategoryDTO;
import com.dscatalogGSilverio.dscatalog.dto.RoleDTO;
import com.dscatalogGSilverio.dscatalog.dto.UserDTO;
import com.dscatalogGSilverio.dscatalog.dto.UserInsertDTO;
import com.dscatalogGSilverio.dscatalog.entities.Category;
import com.dscatalogGSilverio.dscatalog.entities.Role;
import com.dscatalogGSilverio.dscatalog.entities.User;
import com.dscatalogGSilverio.dscatalog.repositories.CategoryRepository;
import com.dscatalogGSilverio.dscatalog.repositories.RoleRepository;
import com.dscatalogGSilverio.dscatalog.repositories.UserRepository;
import com.dscatalogGSilverio.dscatalog.services.exceptions.DatabaseException;
import com.dscatalogGSilverio.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository repository;
    @Autowired
    private RoleRepository roleRepository;
    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable){
        Page<User> list = repository.findAll(pageable);
        /* DIFERENTE DO LIST, O PAGE JÁ É UMA STREAM ENTÃO NÃO É NECESSARIO CONVERTER ELE E DEPOIS RECONVERTER COM O COLLECT
        List<UserDTO>listDTO = list.stream().map(x->new UserDTO(x)).collect(Collectors.toList());*/
        Page<UserDTO>listDTO = list.map(x->new UserDTO(x));
        return listDTO;
    }
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(()->new ResourceNotFoundException("Entity not found"));
        return new UserDTO(entity);

    }
    @Transactional
    public UserDTO insert(UserInsertDTO dto){
        User entity = new User();
        copyDtoToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repository.save(entity);
        return new UserDTO(entity);
    }
    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            //entity.setName(dto.getName());
            entity = repository.save(entity);
            return new UserDTO(entity);

        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id not found"+id);
        }

    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
    private void copyDtoToEntity(UserDTO dto, User entity){
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        entity.getRoles().clear();
        for(RoleDTO roleDto : dto.getRoles()){
            Role role = roleRepository.getReferenceById(roleDto.getId());
            entity.getRoles().add(role);
        }
    }
}
