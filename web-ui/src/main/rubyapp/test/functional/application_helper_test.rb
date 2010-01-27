require 'test_helper'
require 'application_helper'

class ApplicationHelperTest < ActionController::TestCase
  include ApplicationHelper

  def test_get_breadcrumb
    system = systems(:one)
    conversation = conversations(:one)
    
    result = get_breadcrumb({:id => system.id, :action=> 'new'})
    assert_equal "<a href='/'>Home</a> &gt; <span>New system</span>", result
    result = get_breadcrumb({:id => system.id, :action=> 'show'})
    assert_equal "<a href='/'>Home</a> &gt; <span>Edit system</span>", result
    
    result = get_breadcrumb({:system_id => system.id, :action=> 'new'})
    assert_equal "<a href='/'>Home</a> &gt; <a href='/systems/#{system.id}/'>#{system.name}</a> &gt; <span>New conversation</span>", result
    result = get_breadcrumb({:system_id => system.id, :action=> 'show'})
    assert_equal "<a href='/'>Home</a> &gt; <a href='/systems/#{system.id}/'>#{system.name}</a> &gt; <span>Edit conversation</span>", result
    
    result = get_breadcrumb({:system_id => system.id, :conversation_id => conversation.id, :action=> 'new'})
    assert_equal "<a href='/'>Home</a> &gt; <a href='/systems/#{system.id}/'>#{system.name}</a> &gt; <a href='/systems/#{system.id}/conversations/#{conversation.id}/'>#{conversation.name}</a> &gt; <span>New scenario</span>", result
    result = get_breadcrumb({:system_id => system.id, :conversation_id => conversation.id, :action=> 'show'})
    assert_equal "<a href='/'>Home</a> &gt; <a href='/systems/#{system.id}/'>#{system.name}</a> &gt; <a href='/systems/#{system.id}/conversations/#{conversation.id}/'>#{conversation.name}</a> &gt; <span>Edit scenario</span>", result
  end
end