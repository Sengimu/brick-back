package com.brick.yggdrasilserver.service;

import com.brick.yggdrasilserver.entity.Profile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


public interface IProfileService {

    /**
     * 通过用户id查询所有角色
     *
     * @param userId 用户id
     * @return 所有id下角色
     */
    List<Profile> getProfilesByUserId(int userId);

    /**
     * 通过uuid查询角色
     *
     * @param id uuid
     * @return 对应的角色
     */
    Profile getProfileById(String id);

    /**
     * 通过accessToken获取角色
     *
     * @param accessToken accessToken
     * @return 角色
     */
    Profile getProfileByAccessToken(String accessToken);

    /**
     * 通过所有角色名查询角色(不含角色属性)
     *
     * @param names 所有角色名
     * @return 角色集
     */
    List<Profile> getProfilesByProfileName(List<String> names);

    /**
     * 上传材质文件
     *
     * @param file file
     * @param type 类型
     * @return 文件名
     */
    String uploadTextureImage(MultipartFile file, String type);

    /**
     * 插入角色材质
     *
     * @param uuid     id
     * @param type     材质类型
     * @param filename 文件名
     * @param model    粗细模型
     * @return 插入结果
     */
    boolean putTexture(String uuid, String type, String filename, String model);

    /**
     * 删除uuid的材质
     *
     * @param uuid id
     * @return 删除结果
     */
    boolean deleteTexture(String uuid);

    /**
     * 通过角色名获取皮肤材质
     *
     * @param name 角色名
     * @return 皮肤材质集
     */
    List<Map<String, Object>> getTextureByName(String name);
}
