require 'test_helper'
require 'json'

class ConversationsControllerTest < ActionController::TestCase
  fixtures :all

  def test_get_index
    get :index, :system_id => '1'

    # We expect to be redirected to the show template
    assert_response 302
  end

  def test_get_index_json
    get :index, :format => 'json', :system_id => '1'

    assert_response :success
    
    json = JSON.parse(@response.body)
    assert_not_nil json['data']
    assert json['data'].length > 0
  end

  def test_get_show
    get :show

    assert_response :success
    assert_template 'show'
  end

  def test_get_show_json
    get :show, :format => 'json', :id => '1'

    assert_response :success
    json = JSON.parse @response.body

    assert_not_nil(json['data'])
    assert_not_nil(json['configurations'])

    conv = json['data']
    conf = json['configurations']

    assert(conversations(:one).name.eql? conv['name'])
    assert(conversations(:one).inbound_transport_id == conv['inbound_transport_id'])

    assert_not_nil conf['transport_in']
  end

  def test_new_screen
    get :new
    assert_response :success
    assert_template 'show'
  end

  def test_create_should_fail
    # Try creating with out Conversation's required parameters
    post :create,
        :inbound_transport_type_id => 2,
        :outbound_transport_type_id => 1,
        :inbound_format_type_id => 2,
        :outbound_format_type_id => 1

    assert_response :success

    json = JSON.parse(@response.body)
    assert(json['success'] == false)
  end

  def test_create
    assert_difference('Conversation.count', +1) do
      post :create,
        :name => 'New conv',
        :system_id => systems(:one).id,
        :inbound_transport_type_id => 2,
        :outbound_transport_type_id => 1,
        :inbound_format_type_id => 2,
        :outbound_format_type_id => 1,
        :transport_in_host => 'localhost',
        :transport_in_directoryName => 'inbox',
        :transport_out_directoryName => 'outbox',
        :format_in_csvContent => 'employees',
        :format_out_validate => 'true'

      assert_response :success

      json = JSON.parse(@response.body)
      assert json['success'] == true

      conv_id = json['data']['id']

      conv = Conversation.find(conv_id)
      assert_not_nil conv.in_transport
      assert_not_nil conv.out_format
    end
  end

  def test_update_inactive
    #Original
    original = Conversation.find_by_id(conversations(:two).id)
    orig_name = original.name
    orig_in_tran_type = original.in_transport.transport_type_id
    orig_out_form_type = original.out_format.format_type_id

    post :update,
        :id => 2,
        :name => 'Updated conv',
        :system_id => systems(:one).id,
        :inbound_transport_type_id => 5,
        :outbound_transport_type_id => 6,
        :inbound_format_type_id => 2,
        :outbound_format_type_id => 1,
        :transport_in_host => 'localhost',
        :transport_in_directoryName => 'inbox',
        :transport_out_directoryName => 'outbox',
        :format_in_csvContent => 'employees',
        :format_out_validate => 'true'

    assert_response :success

    json = JSON.parse(@response.body)
    assert(json['success'] == true)

    conv = Conversation.find(2)
    assert(!SimulatorConnector.instance.is_active(conv))

    assert(conv.id == original.id)
    assert(conv.name.eql? 'Updated conv')

    trans_in = conv.in_transport
    form_out = conv.out_format

    assert_not_nil(trans_in)
    assert_not_nil(form_out)

    assert(trans_in.transport_type_id.eql?('5'), "Expecting inbound transport type of 5, instead got #{trans_in.transport_type_id}")

    assert_not_nil(TransportConfiguration.find_by_transport_id_and_attribute_name(trans_in.id, 'host'))
    assert_not_nil(FormatConfiguration.find_by_format_id_and_attribute_name(form_out.id, 'validate'))
  end

  def test_update_active
    #Original
    original = Conversation.find_by_id(conversations(:two).id)
    original.enabled=true
    original.save
    SimulatorConnector.instance.activate(original)

    orig_name = original.name
    orig_in_tran_type = original.in_transport.transport_type_id
    orig_out_form_type = original.out_format.format_type_id

    post :update,
        :id => 2,
        :name => 'Updated conv',
        :system_id => systems(:one).id,
        :inbound_transport_type_id => 5,
        :outbound_transport_type_id => 6,
        :inbound_format_type_id => 2,
        :outbound_format_type_id => 1,
        :transport_in_host => 'localhost',
        :transport_in_directoryName => 'inbox',
        :transport_out_directoryName => 'outbox',
        :format_in_csvContent => 'employees',
        :format_out_validate => 'true'

    assert_response :success
    json = JSON.parse(@response.body)
    assert(json['success'] == true)

    conv = Conversation.find(2)
    assert(SimulatorConnector.instance.is_active(conv))

    assert(conv.id == original.id)
    assert(conv.name.eql? 'Updated conv')

    trans_in = conv.in_transport
    form_out = conv.out_format

    assert_not_nil(trans_in)
    assert_not_nil(form_out)

    assert(trans_in.transport_type_id.eql?('5'), "Expecting inbound transport type of 5, instead got #{trans_in.transport_type_id}")

    assert_not_nil(TransportConfiguration.find_by_transport_id_and_attribute_name(trans_in.id, 'host'))
    assert_not_nil(FormatConfiguration.find_by_format_id_and_attribute_name(form_out.id, 'validate'))
    SimulatorConnector.instance.delete_conversation(conv)

  end


  def test_destroy
    assert_difference('Conversation.count', -1) do
      delete :destroy, :id => 1

      assert_response :success

      json = JSON.parse(@response.body)
      assert(json['success'] == true)
    end
  end

  def test_transport_types
    trans_types = TransportType.find(:all)

    get :transport_types

    assert_response :success

    json = JSON.parse(@response.body)

    assert(trans_types.length == json['data'].length)
    assert(trans_types[0].name.eql? json['data'][0]['name'])
  end

  def test_format_types
    form_types = FormatType.find(:all)

    get :format_types

    assert_response :success
    json  = JSON.parse(@response.body)

    assert(form_types.length == json['data'].length)
    assert(form_types[0].name.eql? json['data'][0]['name'])
  end

  def test_transport_parameters
    get :transport_parameters, :type => transport_types(:file).name

    assert_response :success
    json = JSON.parse(@response.body)

    assert(json['data'].length > 0)

    # Parameter defintions should be arrays with 5 strings
    param1 = json['data'][0]
    assert(param1.length == 5)
  end

  def test_format_parameters
    get :format_parameters, :format => format_types(:csv).name

    assert_response :success
    json = JSON.parse(@response.body)

    assert(json['data'].length > 0)

    # Parameter defintions should be arrays with 5 strings
    param1 = json['data'][0]
    assert(param1.length == 5)
  end

  def test_enable_conversation
    # First, create a new conversation
    post :create,
        :name => 'New disabled conversation',
        :system_id => systems(:one).id,
        :inbound_transport_type_id => 2,
        :outbound_transport_type_id => 1,
        :inbound_format_type_id => 2,
        :outbound_format_type_id => 1,
        :transport_in_host => 'localhost',
        :transport_in_directoryName => 'inbox',
        :transport_out_directoryName => 'outbox',
        :format_in_csvContent => 'employees',
        :format_out_validate => 'true'

   assert_response :success
   json = JSON.parse(@response.body)
   json_conv = json['data']

    # Assert we got a disabled new conversation
    assert json['success'] == true
    assert_not_nil json['data']
    assert(!json_conv['enabled'])

    # Enable
    get :enable, :id => json_conv['id']

    assert_response :success
    json = JSON.parse(@response.body)
    json_conv = json['data']

    assert(json_conv['enabled'])
  end

  def test_activate_conversation
    # Original conversation should be disabled
    get :show, :format => 'json', :id => conversations(:one).id

    assert_response :success
    json = JSON.parse @response.body
    assert_not_nil(json['data'])

    json_conv = json['data']
    assert json_conv['enabled'] == false

    # A disabled conversation cannot be activated
    get :activate, :id => json_conv['id']

    assert_response :success
    json = JSON.parse @response.body
    assert_not_nil json['data']
    assert !json['is_active']
  end
end
