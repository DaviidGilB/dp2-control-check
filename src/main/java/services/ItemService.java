
package services;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import domain.Item;
import domain.Provider;
import forms.FormObjectItem;
import repositories.ItemRepository;

@Service
@Transactional
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ProviderService providerService;

	public Item createItem() {

		Item res = new Item();

		List<String> voidList = new ArrayList<String>();

		res.setDescription("");
		res.setLinks(voidList);
		res.setPictures(voidList);
		res.setName("");

		return res;
	}

	public Item findOne(Integer id) {
		return this.itemRepository.findOne(id);
	}

	public Item save(Item item) {
		return this.itemRepository.save(item);
	}

	public List<Item> findAll() {
		return this.itemRepository.findAll();
	}

	public void delete(Item item) {
		this.itemRepository.delete(item);
	}

	public List<Item> getLoggedProviderItems() {
		Provider loggedProvider = this.providerService.securityAndProvider();
		return this.itemRepository.getItemsByProvider(loggedProvider);
	}

	public void addOrUpdateItem(Item item) {
		Provider loggProvider = this.providerService.securityAndProvider();
		List<Item> items = loggProvider.getItems();

		if (item.getId() != 0) {
			Item itemDB = this.getItemOfProvider(item.getId(), loggProvider.getId());
			Assert.notNull(itemDB);
			this.save(item);

		} else {
			Item savedItem = this.save(item);
			items.add(savedItem);
			loggProvider.setItems(items);
			this.providerService.save(loggProvider);
		}

	}

	public FormObjectItem prepareFormObject(int itemId) {
		Provider provider = this.providerService.securityAndProvider();
		Item item = this.getItemOfProvider(itemId, provider.getId());
		Assert.notNull(item);

		FormObjectItem res = new FormObjectItem();

		res.setDescription(item.getDescription());
		res.setName(item.getName());
		res.setId(item.getId());

		String links = "";
		for (String link : item.getLinks())
			links = links + link + ",";
		res.setLinks(links);

		String pictures = "";
		for (String pic : item.getPictures())
			pictures = pictures + pic + ",";
		res.setPictures(pictures);

		return res;
	}

	public Item reconstructItem(FormObjectItem form) {
		Item copy = new Item();
		Item res = new Item();

		if (form.getId() != 0) {
			copy = this.findOne(form.getId());
			res.setId(copy.getId());
			res.setVersion(copy.getVersion());
		}

		res.setDescription(form.getDescription());
		res.setName(form.getName());

		String[] link = form.getLinks().trim().split(",");
		List<String> links = Arrays.asList(link);

		if (links.size() == 1 && links.get(0).isEmpty())
			res.setLinks(new ArrayList<String>());
		else
			res.setLinks(links);

		String[] pic = form.getPictures().trim().split(",");
		List<String> pictures = Arrays.asList(pic);

		if (pictures.size() == 1 && pictures.get(0).isEmpty())
			res.setPictures(new ArrayList<String>());
		else
			res.setPictures(pictures);

		return res;
	}

	public void deleteItem(int itemId) {
		Provider provider = this.providerService.securityAndProvider();
		Item item = this.getItemOfProvider(itemId, provider.getId());
		Assert.notNull(item);

		List<Item> items = provider.getItems();
		items.remove(item);
		provider.setItems(items);

		this.providerService.save(provider);
		this.delete(item);

	}
	
	public List<Item> getItemsFromProvider(int providerId){
		return this.itemRepository.getItemsFromProvider(providerId);
	}

	private Item getItemOfProvider(int itemId, int providerId) {
		return this.itemRepository.getItemOfProvider(itemId, providerId);
	}

	public List<String> getLinksOfItem(int itemId) {
		Provider loggedProvider = this.providerService.securityAndProvider();
		return this.itemRepository.getLinksOfItem(itemId, loggedProvider.getId());
	}

	public List<String> getPicturesOfItem(int itemId) {
		Provider loggedProvider = this.providerService.securityAndProvider();
		return this.itemRepository.getPicturesOfItem(itemId, loggedProvider.getId());
	}

	public void deleteInBatch(Iterable<Item> lista) {
		this.itemRepository.deleteInBatch(lista);
	}
	
	public Provider geProviderByItem(int itemId) {
		return this.itemRepository.getProviderByItem(itemId);
	}
	
	public Map<Item, Provider> getProvidersByItem(List<Item> items){
		
		Map<Item, Provider> res = new HashMap<Item, Provider>();
		for(Item i: items) {
			res.put(i, this.geProviderByItem(i.getId()));
		}
		return res;
	}

	public Boolean isUrl(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
